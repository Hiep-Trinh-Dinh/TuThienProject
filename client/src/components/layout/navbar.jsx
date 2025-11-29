"use client"

import { useAuth } from "../../contexts/auth-context"
import { Button } from "../ui/button"
import { Link, useNavigate } from "react-router-dom"
import { Heart, User, LogOut , UserSquare, LockKeyholeIcon, UserPen, BookHeart } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "../ui/dropdown-menu"
import { useEffect } from "react"

export function Navbar() {
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");

    if (token) {
      login(token);
    }
  }, []);
  const { user, logout, login, isAuthenticated, loading} = useAuth()
  const navigate = useNavigate();
  if (loading) {
    return (<div className="spinner-container">
            <div className="spinner"></div>
            </div>
      );
  }

  const roles = ["admin", "vip_user"];
  return (
    <nav className="bg-white border-b border-border sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="flex items-center gap-2">
            <Heart className="h-8 w-8 text-primary" />
            <span className="text-xl font-bold text-primary">Hip</span>
          </Link>

          <div className="hidden md:flex items-center gap-6">
            <Link to="/" className="text-foreground hover:text-primary transition-colors">
              Home
            </Link>
            <Link to="/projects" className="text-foreground hover:text-primary transition-colors">
              Projects
            </Link>
            <Link to="/about" className="text-foreground hover:text-primary transition-colors">
              About
            </Link>
          </div>

          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="flex items-center gap-2">
                    <User className="h-4 w-4" />
                    {/* {user?.email || "User"} */}
                    <strong>{user.fullName}</strong> 
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem onClick={()=>{navigate("/");logout(localStorage.getItem("token"));}} className="text-destructive">
                    <LogOut className="h-4 w-4 mr-2" />
                    Logout
                  </DropdownMenuItem>

                  <DropdownMenuItem asChild>
                    <Link to={`/account`} className="flex items-center">
                      <UserSquare className="h-4 w-4 mr-2" />
                      View profile
                    </Link>
                  </DropdownMenuItem>

                  <DropdownMenuItem asChild>
                    <Link to={`/account/profile/${user.userId}`} className="flex items-center">
                      <UserPen className="h-4 w-4 mr-2" />
                      Edit personal information
                    </Link>
                  </DropdownMenuItem>

                  {roles.includes(user.roles[0].name) ? (
                    <DropdownMenuItem asChild>
                      <Link to={`/account/pending-camps`} className="flex items-center">
                        <BookHeart className="h-4 w-4 mr-2" />
                      Project management
                      </Link>
                    </DropdownMenuItem>
                  ) : (
                    <></>
                  )}
                  {user.authProvider === "LOCAL" ? 
                  (<DropdownMenuItem asChild>
                    <Link to={`/account/password/${user.userId}`} className="flex items-center">
                      <LockKeyholeIcon className="h-4 w-4 mr-2" />
                      Change password
                    </Link>
                  </DropdownMenuItem>)
                  :(
                    ""
                  )}
                </DropdownMenuContent>
              </DropdownMenu>
            ) : (
              <div className="flex items-center gap-2">
                <Button variant="ghost" asChild>
                  <Link to="/login">Sign In</Link>
                </Button>
                <Button asChild>
                  <Link to="/register">Sign Up</Link>
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}

export default Navbar
