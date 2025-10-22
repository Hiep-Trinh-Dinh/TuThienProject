import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/auth-context";
import "./spinner.css";
const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth(); // lấy thông tin user từ context
  if (loading) {
    return (<div className="spinner-container">
            <div className="spinner"></div>
            </div>
      );
  }
  // Nếu chưa login then chuyển hướng sang trang /login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Nếu đã login then render component con (children)
  return children;
};

export default ProtectedRoute;
