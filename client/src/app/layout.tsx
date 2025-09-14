import type React from "react"
import type { Metadata } from "next"
import { GeistSans } from "geist/font/sans"
import { GeistMono } from "geist/font/mono"
import { Analytics } from "@vercel/analytics/next"
import { AuthProvider } from "../contexts/auth-context"
import { Navbar } from "../components/layout/navbar"
import { Suspense } from "react"
import "./globals.css"

export const metadata: Metadata = {
  title: "CharityHub - Making a Difference Together",
  description: "Join our mission to support meaningful causes and create positive change in the world",
  generator: "v0.app",
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="en">
      <body className={`font-sans ${GeistSans.variable} ${GeistMono.variable}`}>
        <AuthProvider>
          <Suspense fallback={<div>Loading...</div>}>
            <Navbar />
            <main className="min-h-screen">{children}</main>
          </Suspense>
        </AuthProvider>
        <Analytics />
      </body>
    </html>
  )
}
