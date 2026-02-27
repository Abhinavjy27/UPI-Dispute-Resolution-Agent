# UPI Dispute Resolution - Frontend

Modern React frontend for the UPI Dispute Resolution Agent with shadcn/ui components and Tailwind CSS.

## 🚀 Features

- **Hero Section**: Beautiful animated landing page with gradient effects
- **Dispute Form**: Validated form for filing UPI disputes with real-time validation
- **Status Tracker**: Track dispute status with copy-to-clipboard functionality
- **Responsive Design**: Mobile-first design that works on all devices
- **Dark Mode Support**: Full dark mode with automatic detection
- **TypeScript**: Full type safety throughout the application

## 📦 Tech Stack

- **React 19** - UI library
- **TypeScript** - Type safety
- **Vite 8** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **shadcn/ui** - Re-usable component library
- **Axios** - HTTP client for API calls
- **Lucide React** - Beautiful icon set

## 🛠️ Setup Instructions

### Prerequisites

- Node.js 18+ installed
- npm or yarn package manager

### Installation

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Configure environment variables**:
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` and set:
   ```
   VITE_API_BASE_URL=http://localhost:8000
   ```

3. **Start development server**:
   ```bash
   npm run dev
   ```

   The app will be available at `http://localhost:5173`

### Build for Production

```bash
npm run build
npm run preview  # Preview production build locally
```

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── ui/               # shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── label.tsx
│   │   │   └── hero-section.tsx
│   │   ├── DisputeForm.tsx   # Main dispute submission form
│   │   └── StatusTracker.tsx # Dispute status tracking
│   ├── lib/
│   │   ├── utils.ts          # Utility functions (cn helper)
│   │   └── api.ts            # API configuration and endpoints
│   ├── App.tsx               # Main application component
│   ├── main.tsx              # Application entry point
│   └── index.css             # Global styles with Tailwind
├── public/                   # Static assets
├── .env                      # Environment variables
├── .env.example              # Environment variables template
├── package.json              # Dependencies and scripts
├── tailwind.config.js        # Tailwind CSS configuration
├── tsconfig.json             # TypeScript configuration
└── vite.config.ts            # Vite configuration
```

## 🎨 Components

### HeroSection
Animated landing section with retro grid background and gradient text.

**Props**:
- `title` - Badge title text
- `subtitle` - Main heading with regular and gradient parts
- `description` - Subheading text
- `ctaText` - Call-to-action button text
- `ctaHref` - CTA button link
- `gridOptions` - Customization for retro grid animation

### DisputeForm
Form for filing new disputes with validation.

**Features**:
- Real-time field validation
- Transaction ID format: `TXN` + 14 digits
- UPI format: `xxx@upi` or `xxx@bank`
- Amount range: ₹1 to ₹100,000
- Phone format: `+91` + 10 digits
- Loading states and error handling
- Success callback with dispute ID

### StatusTracker
Component to track dispute status.

**Features**:
- Search by dispute ID
- Status badges with colors
- Dispute details display
- NEFT reference with copy-to-clipboard
- Timeline visualization
- Responsive layout

## 🌐 API Integration

The app connects to the backend API at `http://localhost:8000/api`

### Endpoints Used:

- `POST /api/disputes` - Submit new dispute
- `GET /api/disputes/{id}` - Get dispute status
- `GET /api/health` - Health check

See `src/lib/api.ts` for full API configuration.

## 🎯 Form Validation Rules

### Transaction ID
- Required
- Format: `TXN` followed by at least 14 digits
- Example: `TXN20260227123456`

### Merchant UPI
- Required
- Format: `username@provider`
- Example: `amazon@upi` or `merchant@bank`

### Amount
- Required
- Range: ₹1 to ₹100,000
- Numeric only

### Phone Number
- Required
- Format: `+91` followed by 10 digits
- Example: `+919876543210`

## 🎨 Styling

### Tailwind CSS
The project uses Tailwind CSS with a custom theme configured in `tailwind.config.js`.

### Color Scheme
- Primary: Purple/Indigo (`#8B5CF6`)
- Success: Green (`#10B981`)
- Error: Red (`#EF4444`)
- Warning: Yellow (`#F59E0B`)

### Dark Mode
Automatic dark mode support with `class` strategy. Toggle with system preferences.

## 🔧 Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

### Hot Module Replacement (HMR)
Vite provides instant hot reload during development.

### Type Checking
TypeScript configured with strict mode for maximum type safety.

## 📱 Responsive Breakpoints

```css
sm:  640px   /* Mobile landscape */
md:  768px   /* Tablet */
lg:  1024px  /* Desktop */
xl:  1280px  /* Large desktop */
```

## 🚨 Error Handling

The app handles various error scenarios:
- Network errors (backend unavailable)
- Validation errors (form inputs)
- API errors (404, 500, etc.)
- Loading states for async operations

## 🔐 Security

- Input sanitization on all form fields
- HTTPS in production (configure in deployment)
- Environment variables for sensitive config
- CORS configured on backend

## 📸 Screenshots

### Home Page
Hero section with animated grid background and call-to-action buttons.

### Dispute Form
Clean form with real-time validation and helpful error messages.

### Status Tracker
Status display with timeline, details, and NEFT information.

## 🤝 Contributing

1. Follow the existing code style
2. Use TypeScript for all new files
3. Add proper types and interfaces
4. Test responsiveness on mobile
5. Ensure dark mode compatibility

## 📄 License

See LICENSE file in root directory.

## 🆘 Troubleshooting

### Port 5173 already in use
```bash
# Kill the process or use a different port
npm run dev -- --port 3000
```

### Dependencies not installing
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

### TypeScript errors
```bash
# Restart TypeScript server in VS Code
# Or rebuild
npm run build
```

### API connection failed
- Ensure backend is running on `http://localhost:8000`
- Check `.env` file has correct `VITE_API_BASE_URL`
- Verify CORS is configured on backend

## 📞 Support

For issues or questions, file a GitHub issue or contact the development team.

---

Built with ❤️ for the UPI Dispute Resolution Hackathon
