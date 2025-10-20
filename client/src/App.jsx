import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/layout/navbar'
import Footer from './components/layout/footer'
import Home from './components/home/hero-section'
import Projects from './components/projects/projects-listing'
import RegisterPage from './app/register/page'
import LoginPage from './app/login/page'
import About from './components/home/about-section'
import ProjectDetailsPage from './app/projects/[id]/page'
import { AuthProvider } from "./contexts/auth-context"
import './styles/globals.css'
import ProfileSection from './app/profile/profile-section'
import ChangePassword from './app/profile/change-password'
import ProtectedRoute from './components/auth/ProtectedRoute'
import VerifyMailForm from './app/ForgotPassword/verify-mail'
import VerifyOtpForm from './app/ForgotPassword/verify-otp'
import ChangePasswordForm from './app/ForgotPassword/change-password'
function App() {
  return (
    <AuthProvider>
    <BrowserRouter>
      <Navbar />
      <Routes>
        {/* Public routes */}
          <Route path="/" element={<Home />} />
          <Route path="/projects" element={<Projects />} />
          <Route path="/projects/:id" element={<ProjectDetailsPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/about" element={<About />} />
          <Route path="/verifyemail" element={<VerifyMailForm />} />
          <Route path="/verifyotp/:email" element={<VerifyOtpForm />} />
          <Route path="/changepassword/:email" element={<ChangePasswordForm />} />

          {/* Protected routes */}
          <Route
            path="/profile/:id"
            element={
              <ProtectedRoute>
                <ProfileSection />
              </ProtectedRoute>
            }
          />
          <Route
            path="/password/:id"
            element={
              <ProtectedRoute>
                <ChangePassword />
              </ProtectedRoute>
            }
          />
      </Routes>
      <Footer />
    </BrowserRouter>
    </AuthProvider>
  )
}

export default App
