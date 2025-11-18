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
import EditProfileSection from './app/profile/edit-profile-section'
import ChangePassword from './app/profile/change-password'
import ProtectedRoute from './components/auth/ProtectedRoute'
import VerifyMailForm from './app/ForgotPassword/verify-mail'
import VerifyOtpForm from './app/ForgotPassword/verify-otp'
import ChangePasswordForm from './app/ForgotPassword/change-password'
import AccountSection from './app/profile/account'
import PendingCampSection from './app/profile/pending-camps'

// import admin layout + pages
import AdminLayout from './app/admin/layout'
import AdminDashboard from './app/admin/page'
import AdminProjectList from './app/admin/projects'
import AdminUserList from './app/admin/users'
import AdminDonationsList from './app/admin/donations'

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
          <Route path="/account" element={<AccountSection />} />
          <Route path="/account/pending-camps/" element={<PendingCampSection />} />

          {/* Protected routes */}
          <Route
            path="/account/profile/:id"
            element={
              <ProtectedRoute>
                <EditProfileSection />
              </ProtectedRoute>
            }
          />
          <Route
            path="/account/password/:id"
            element={
              <ProtectedRoute>
                <ChangePassword />
              </ProtectedRoute>
            }
          />

          {/* ADMIN ROUTES */}
          <Route path="/admin" element={
            <AdminLayout>
              <AdminDashboard />
            </AdminLayout>
          } />
          <Route path="/admin/projects" element={
            <AdminLayout>
              <AdminProjectList />
            </AdminLayout>
          } />
          <Route path="/admin/users" element={
            <AdminLayout>
              <AdminUserList />
            </AdminLayout>
          } />
          <Route path="/admin/donations" element={
            <AdminLayout>
              <AdminDonationsList />
            </AdminLayout>
          } />
      </Routes>
      <Footer />
    </BrowserRouter>
    </AuthProvider>
  )
}

export default App
