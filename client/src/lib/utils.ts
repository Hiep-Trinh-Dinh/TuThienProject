import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

const DEFAULT_API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function resolveProjectImage(imageUrl?: string | null) {
  if (!imageUrl) return "/placeholder.svg"

  if (imageUrl.startsWith("data:") || imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
    return imageUrl
  }

  if (imageUrl.startsWith("/")) {
    return `${DEFAULT_API_BASE}${imageUrl}`
  }

  return `${DEFAULT_API_BASE}/${imageUrl}`
}
