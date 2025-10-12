import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/layout/navbar'
import Footer from './components/layout/footer'
import Home from './components/home/hero-section'
import Projects from './components/projects/projects-listing'
import Register from './components/auth/register-form'
import Login from './components/auth/login-form'
import About from './components/home/about-section'
import ProjectDetailsPage from './app/projects/[id]/page'
import { AuthProvider } from "./contexts/auth-context"
import './styles/globals.css'
import ProfileSection from './app/profile/profile-section'
import ChangePassword from './app/profile/change-password'
import ProtectedRoute from './components/auth/ProtectedRoute'
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
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/about" element={<About />} />

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
