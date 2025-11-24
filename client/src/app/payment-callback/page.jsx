import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import axios from "../../axiosConfig";

export default function PaymentCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState("processing"); // processing, success, failed
  const [message, setMessage] = useState("Đang xử lý thanh toán...");

  useEffect(() => {
    const handlePaymentCallback = async () => {
      try {
        // Lấy params từ URL
        const resultCode = searchParams.get("resultCode");
        const orderId = searchParams.get("orderId");
        const extraData = searchParams.get("extraData");
        const orderInfo = searchParams.get("orderInfo");
        const message = searchParams.get("message");

        console.log("[PAYMENT_CALLBACK] Nhận callback từ Momo:", {
          resultCode,
          orderId,
          extraData,
          orderInfo,
        });

        // Nếu thanh toán thành công
        if (resultCode === "0") {
          setStatus("processing");
          setMessage("Thanh toán thành công! Đang cập nhật trạng thái...");

          // Gọi API backend để verify và update payment status
          try {
            const response = await axios.post("/payment/verify", {
              orderId,
              extraData,
              resultCode,
              orderInfo,
            });

            console.log("[PAYMENT_CALLBACK] Backend response:", response.data);

            const projectId = response.data?.projectId || extractProjectIdFromOrderInfo(orderInfo);

            if (projectId) {
              setStatus("success");
              setMessage("Thanh toán thành công! Đang chuyển đến trang dự án...");
              
              // Redirect đến trang chi tiết dự án sau 1 giây
              setTimeout(() => {
                navigate(`/projects/${projectId}?payment=success`);
              }, 1000);
            } else {
              setStatus("success");
              setMessage("Thanh toán thành công! Đang chuyển đến danh sách dự án...");
              setTimeout(() => {
                navigate("/projects?payment=success");
              }, 1000);
            }
          } catch (error) {
            console.error("[PAYMENT_CALLBACK] Lỗi khi verify payment:", error);
            // Vẫn redirect dù có lỗi verify
            const projectId = extractProjectIdFromOrderInfo(orderInfo);
            if (projectId) {
              setTimeout(() => {
                navigate(`/projects/${projectId}?payment=success`);
              }, 1000);
            } else {
              setTimeout(() => {
                navigate("/projects?payment=success");
              }, 1000);
            }
          }
        } else {
          // Thanh toán thất bại
          setStatus("failed");
          setMessage(message || "Thanh toán thất bại");
          
          const projectId = extractProjectIdFromOrderInfo(orderInfo);
          setTimeout(() => {
            if (projectId) {
              navigate(`/projects/${projectId}?payment=failed`);
            } else {
              navigate("/projects?payment=failed");
            }
          }, 2000);
        }
      } catch (error) {
        console.error("[PAYMENT_CALLBACK] Lỗi:", error);
        setStatus("failed");
        setMessage("Có lỗi xảy ra khi xử lý thanh toán");
        setTimeout(() => {
          navigate("/projects");
        }, 2000);
      }
    };

    handlePaymentCallback();
  }, [searchParams, navigate]);

  // Extract projectId từ orderInfo
  const extractProjectIdFromOrderInfo = (orderInfo) => {
    if (!orderInfo) return null;
    const match = orderInfo.match(/\d+$/);
    return match ? parseInt(match[0]) : null;
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8 text-center">
        {status === "processing" && (
          <>
            <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <h2 className="text-2xl font-semibold text-gray-800 mb-2">Đang xử lý...</h2>
            <p className="text-gray-600">{message}</p>
          </>
        )}
        {status === "success" && (
          <>
            <div className="text-6xl mb-4">✅</div>
            <h2 className="text-2xl font-semibold text-green-600 mb-2">Thanh toán thành công!</h2>
            <p className="text-gray-600">{message}</p>
          </>
        )}
        {status === "failed" && (
          <>
            <div className="text-6xl mb-4">❌</div>
            <h2 className="text-2xl font-semibold text-red-600 mb-2">Thanh toán thất bại</h2>
            <p className="text-gray-600">{message}</p>
          </>
        )}
      </div>
    </div>
  );
}

