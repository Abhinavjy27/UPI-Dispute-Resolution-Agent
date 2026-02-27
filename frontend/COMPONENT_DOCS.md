# Component Documentation

## 📂 Component Structure

The project follows the **shadcn/ui** pattern with components in `/src/components/ui/` folder.

### Why `/components/ui/`?

This is the **standard shadcn/ui convention**:
- `/components/ui/` - Base UI components (buttons, inputs, labels)
- `/components/` - Feature components (DisputeForm, StatusTracker)

This separation keeps base components reusable across the app.

## 🎨 shadcn/ui Components

### Button Component
**Location**: `src/components/ui/button.tsx`

**Usage**:
```tsx
import { Button } from "@/components/ui/button"

<Button variant="default" size="lg">
  Click me
</Button>
```

**Variants**:
- `default` - Primary purple button
- `destructive` - Red button for dangerous actions
- `outline` - Bordered transparent button
- `secondary` - Gray button
- `ghost` - No background, hover effect only
- `link` - Text link style

**Sizes**:
- `default` - h-10, medium padding
- `sm` - h-9, smaller padding
- `lg` - h-11, larger padding
- `icon` - Square button for icons

### Input Component
**Location**: `src/components/ui/input.tsx`

**Usage**:
```tsx
import { Input } from "@/components/ui/input"

<Input
  type="text"
  placeholder="Enter value"
  value={value}
  onChange={(e) => setValue(e.target.value)}
/>
```

**Features**:
- Full TypeScript support
- Focus ring styling
- Disabled state styling
- Dark mode support

### Label Component
**Location**: `src/components/ui/label.tsx`

**Usage**:
```tsx
import { Label } from "@/components/ui/label"

<Label htmlFor="email">Email Address</Label>
<Input id="email" type="email" />
```

**Features**:
- Semantic HTML labels
- Proper accessibility (links to input via htmlFor)
- Consistent styling with forms

## 🏗️ Custom Components

### HeroSection Component
**Location**: `src/components/ui/hero-section.tsx`

**Description**: Animated landing section with retro grid background, gradient text, and call-to-action button.

**Usage**:
```tsx
import { HeroSection } from "@/components/ui/hero-section"

<HeroSection
  title="Fast & Secure UPI Dispute Resolution"
  subtitle={{
    regular: "Resolve failed UPI transactions in ",
    gradient: "2-4 hours, not 5-7 days.",
  }}
  description="Automated AI-powered system..."
  ctaText="File Dispute Now"
  ctaHref="#dispute-form"
  gridOptions={{
    angle: 65,
    opacity: 0.4,
    cellSize: 50,
  }}
/>
```

**Props**:
```typescript
interface HeroSectionProps {
  title?: string              // Badge title at top
  subtitle?: {
    regular: string          // First part of heading
    gradient: string         // Gradient colored part
  }
  description?: string       // Subheading paragraph
  ctaText?: string          // Button text
  ctaHref?: string          // Button link
  bottomImage?: {           // Optional dashboard image
    light: string
    dark: string
  }
  gridOptions?: {           // Grid animation customization
    angle?: number          // Grid rotation angle (deg)
    cellSize?: number       // Size of grid cells (px)
    opacity?: number        // Grid opacity (0-1)
    lightLineColor?: string // Light mode grid color
    darkLineColor?: string  // Dark mode grid color
  }
}
```

**Customization**:
```tsx
// Change grid animation
<HeroSection
  gridOptions={{
    angle: 45,           // Tilt angle
    cellSize: 80,        // Larger cells
    opacity: 0.6,        // More visible
    lightLineColor: "#3a3a3a",
    darkLineColor: "#1a1a1a",
  }}
/>
```

### DisputeForm Component
**Location**: `src/components/DisputeForm.tsx`

**Description**: Complete form for filing UPI disputes with real-time validation, error handling, and success states.

**Usage**:
```tsx
import { DisputeForm } from "@/components/DisputeForm"

<DisputeForm
  onSuccess={(disputeId) => {
    console.log("Dispute created:", disputeId)
    // Navigate to status page
  }}
/>
```

**Props**:
```typescript
interface DisputeFormProps {
  onSuccess?: (disputeId: string) => void  // Callback when form submits successfully
}
```

**Validation Rules**:

1. **Transaction ID**:
   - Required field
   - Pattern: `^TXN\d{14,}$`
   - Example: `TXN20260227123456`

2. **Merchant UPI**:
   - Required field
   - Pattern: `^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$`
   - Example: `amazon@upi`, `merchant@bank`

3. **Amount**:
   - Required field
   - Range: 1 to 100,000
   - Type: Number

4. **Phone Number**:
   - Required field
   - Pattern: `^\+91\d{10}$`
   - Example: `+919876543210`

**State Management**:
```typescript
const [formData, setFormData] = useState<FormData>({
  transactionId: '',
  merchantUpi: '',
  amount: '',
  customerPhone: '+91',
})

const [errors, setErrors] = useState<FormErrors>({})
const [loading, setLoading] = useState(false)
const [submitError, setSubmitError] = useState<string | null>(null)
const [success, setSuccess] = useState(false)
```

**API Integration**:
```typescript
// Calls POST /api/disputes
const response = await disputeApi.createDispute({
  transactionId: formData.transactionId,
  merchantUpi: formData.merchantUpi,
  amount: parseFloat(formData.amount),
  customerPhone: formData.customerPhone,
})
```

### StatusTracker Component
**Location**: `src/components/StatusTracker.tsx`

**Description**: Component to search and display dispute status with timeline, NEFT details, and copy-to-clipboard functionality.

**Usage**:
```tsx
import { StatusTracker } from "@/components/StatusTracker"

// With initial dispute ID
<StatusTracker disputeId="DIS_1709028600" />

// Without ID (shows search form)
<StatusTracker />
```

**Props**:
```typescript
interface StatusTrackerProps {
  disputeId?: string  // Optional initial dispute ID
}
```

**Features**:

1. **Search Functionality**:
   - Input to enter dispute ID
   - Search button with loading state
   - Error handling for invalid IDs

2. **Status Display**:
   - Color-coded status badges:
     - 🟢 Green: `REFUND_INITIATED`
     - 🔴 Red: `REJECTED`
     - 🟡 Yellow: `MANUAL_REVIEW`
     - 🔵 Blue: `PENDING`

3. **Details Section**:
   - Dispute ID (copyable)
   - Transaction ID
   - Amount in INR format
   - Merchant UPI
   - Created timestamp

4. **NEFT Information** (if refund initiated):
   - NEFT reference number
   - Copy to clipboard button
   - Expected settlement time (24-48h)

5. **Timeline**:
   - Visual timeline of dispute progress
   - Checkmarks for completed steps
   - Clock icon for pending steps

**API Integration**:
```typescript
// Calls GET /api/disputes/{id}
const response = await disputeApi.getDisputeById(id)
setDisputeStatus(response.data)
```

**Clipboard Feature**:
```typescript
const copyToClipboard = (text: string, type: string) => {
  navigator.clipboard.writeText(text)
  setCopied(type)
  setTimeout(() => setCopied(null), 2000)
}
```

## 🔧 Utility Functions

### cn() Helper
**Location**: `src/lib/utils.ts`

**Description**: Combines `clsx` and `tailwind-merge` for conditional className management.

**Usage**:
```tsx
import { cn } from "@/lib/utils"

<div className={cn(
  "base-class",
  condition && "conditional-class",
  anotherCondition ? "true-class" : "false-class"
)} />
```

**Why?**:
- Merges Tailwind classes intelligently
- Removes conflicting classes
- Handles conditional classes cleanly

**Example**:
```tsx
// Without cn()
<Button className={`${baseClasses} ${loading ? 'opacity-50' : ''} ${error ? 'border-red-500' : ''}`} />

// With cn()
<Button className={cn(baseClasses, loading && 'opacity-50', error && 'border-red-500')} />
```

## 🌐 API Configuration

### API Client
**Location**: `src/lib/api.ts`

**Description**: Axios instance configured for backend communication.

**Configuration**:
```typescript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000'

export const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
})
```

**Interceptors**:
```typescript
// Request interceptor - adds auth headers
api.interceptors.request.use((config) => {
  // Add auth token if available
  return config
})

// Response interceptor - handles errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data)
    return Promise.reject(error)
  }
)
```

**Endpoints**:
```typescript
export const disputeApi = {
  createDispute: (data) => api.post('/disputes', data),
  getDisputeById: (id) => api.get(`/disputes/${id}`),
  healthCheck: () => api.get('/health'),
}
```

**Usage**:
```typescript
import { disputeApi } from '@/lib/api'

// Create dispute
const response = await disputeApi.createDispute({
  transactionId: "TXN20260227123456",
  merchantUpi: "amazon@upi",
  amount: 5000,
  customerPhone: "+919876543210"
})

// Get dispute status
const status = await disputeApi.getDisputeById("DIS_1709028600")
```

## 🎨 Styling Guidelines

### Tailwind Classes

**Common Patterns**:
```tsx
// Card/Container
className="p-6 bg-card rounded-lg border shadow-lg"

// Form Input
className="w-full border rounded-md px-3 py-2"

// Button
className="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary/90"

// Error Text
className="text-sm text-destructive"

// Muted Text
className="text-sm text-muted-foreground"
```

### Responsive Design
```tsx
// Mobile first approach
className="
  px-4          // Small screens
  md:px-8       // Medium screens (768px+)
  lg:px-12      // Large screens (1024px+)
"

// Grid responsive
className="
  grid
  grid-cols-1         // 1 column on mobile
  md:grid-cols-2      // 2 columns on tablet
  lg:grid-cols-3      // 3 columns on desktop
  gap-4
"
```

### Dark Mode
```tsx
// Background
className="bg-white dark:bg-gray-950"

// Text
className="text-gray-900 dark:text-white"

// Border
className="border-gray-200 dark:border-gray-800"
```

## 🔍 TypeScript Interfaces

### Form Data
```typescript
interface FormData {
  transactionId: string
  merchantUpi: string
  amount: string
  customerPhone: string
}

interface FormErrors {
  transactionId?: string
  merchantUpi?: string
  amount?: string
  customerPhone?: string
}
```

### Dispute Status
```typescript
interface DisputeStatus {
  disputeId: string
  transactionId: string
  merchantUpi: string
  amount: number
  customerPhone: string
  status: 'REFUND_INITIATED' | 'REJECTED' | 'MANUAL_REVIEW' | 'PENDING'
  message: string
  neftReferenceNumber?: string
  createdAt: string
  updatedAt?: string
}
```

## ✅ Component Checklist

When creating new components:

- [ ] Place in correct folder (`/ui/` for base, `/components/` for features)
- [ ] Add TypeScript interfaces for props
- [ ] Use `cn()` for className management
- [ ] Support dark mode with `dark:` classes
- [ ] Make responsive with `md:` and `lg:` breakpoints
- [ ] Add proper accessibility attributes (aria-*, role)
- [ ] Handle loading and error states
- [ ] Document with JSDoc comments
- [ ] Export from component file

---

**Questions?** Check FRONTEND_README.md or SETUP_GUIDE.md for more info!
