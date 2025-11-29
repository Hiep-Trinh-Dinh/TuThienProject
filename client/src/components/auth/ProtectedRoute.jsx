import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/auth-context";
import PendingCampSection from '../../app/profile/pending-camps'

import "./spinner.css";
const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth(); // lấy thông tin user từ context
  const roles = ["admin", "vip_user"];
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
  
  if(children.type === PendingCampSection && !roles.includes(user.roles[0].name))
    return <Navigate to="/" replace />;

  // Nếu đã login then render component con (children)
  return children;
};

export default ProtectedRoute;
