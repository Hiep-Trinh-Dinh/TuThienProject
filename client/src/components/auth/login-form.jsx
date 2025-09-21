"use client"
import api from "../../axiosConfig"
import { useState} from "react"
// import { useAuth } from "../../contexts/auth-context"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card"
import { Alert, AlertDescription } from "../ui/alert"
import { Link,useNavigate } from "react-router-dom"
import { useAuth } from "../../contexts/auth-context"

export function LoginForm() {
  // const [email, setEmail] = useState("")
  // const [password, setPassword] = useState("")
  const { login } = useAuth()
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  // const { login } = useAuth()
  const navigate = useNavigate();
  const [form, setForm] = useState({email:"",
                                    password:""});
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name] : e.target.value});
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError("")
    setLoading(true)

    try {
      // const res = await api.post("/auth/login", form);
      // localStorage.setItem("token", res.data.token);
      // localStorage.setItem("charity-user", JSON.stringify(res.data.user));
      // navigate("/");
      const res = await api.post("/auth/login", form);
      login(res.data.user);
      localStorage.setItem("token", res.data.token);
      navigate("/");
    } catch (error) {
      if(error.response && error.response.status == 403){
        alert("Incorrect email address or password.");
      }else{
        alert("Login failed. Please try again.");
      }
    }
    // const result = await login(email, password)

    // if (!result.success) {
    //   setError(result.error)
    // }

    setLoading(false)
  }

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader className="text-center">
        <CardTitle className="text-2xl font-bold text-primary">Welcome Back</CardTitle>
        <CardDescription>Sign in to your account to continue supporting causes</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
            />
          </div>

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </Button>

          {/* <Button type="submit" className="w-full">
            Sign In
          </Button> */}

          <div className="text-center text-sm text-muted-foreground">
            Don't have an account?{" "}
            <Link to="/register" className="text-primary hover:underline">
              Sign up here
            </Link>
          </div>

          <div className="text-xs text-muted-foreground bg-muted p-3 rounded">
            <strong>Demo credentials:</strong>
            <br />
            Email: john@example.com
            <br />
            Password: password123
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

export default LoginForm
