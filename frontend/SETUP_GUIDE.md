# 🚀 Frontend Quick Setup Guide

## Step 1: Install Dependencies

```bash
cd frontend
npm install
```

This will install all required packages:
- React 19
- TypeScript
- Vite 8
- Tailwind CSS
- shadcn/ui components
- Axios
- Lucide icons

## Step 2: Configure Environment

```bash
# Copy environment template
cp .env.example .env

# Edit .env file (optional - defaults work for local development)
# VITE_API_BASE_URL=http://localhost:8000
```

## Step 3: Run Development Server

```bash
npm run dev
```

The app will start at: **http://localhost:5173**

## 🎯 What You'll See

1. **Landing Page**: Hero section with animated grid background
2. **File Dispute Button**: Click to open the dispute form
3. **Track Status Button**: Click to search for existing disputes

## 📝 Testing the App

### Without Backend (Frontend Only)

The form will show validation errors when you try to submit without a running backend.

### With Backend Running

1. Make sure backend is running on `http://localhost:8000`
2. Fill out the dispute form:
   - **Transaction ID**: `TXN20260227123456`
   - **Merchant UPI**: `amazon@upi`
   - **Amount**: `5000`
   - **Phone**: `+919876543210`
3. Click "Submit Dispute"
4. You'll be redirected to the status page showing your dispute details

## 🔧 Troubleshooting

### Issue: "npm install" fails
**Solution**:
```bash
# Clear cache
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### Issue: Port 5173 is already in use
**Solution**:
```bash
# Use a different port
npm run dev -- --port 3000
```

### Issue: API calls fail with CORS error
**Solution**:
- Ensure backend is running on `http://localhost:8000`
- Check backend has CORS configured for `http://localhost:5173`

### Issue: TypeScript errors in VS Code
**Solution**:
- Restart VS Code
- Or run: `npm run build` to check for errors

## 📦 Production Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

Built files will be in `dist/` folder.

## 🎨 Customization

### Change Colors

Edit `tailwind.config.js`:
```javascript
theme: {
  extend: {
    colors: {
      primary: "your-color-here"
    }
  }
}
```

### Modify Hero Section

Edit `src/components/ui/hero-section.tsx` to customize:
- Title text
- Subtitle
- Description
- Button text
- Grid animation settings

### Update Form Validation

Edit `src/components/DisputeForm.tsx`:
- Modify `validateField()` function for custom rules
- Change min/max amounts
- Adjust phone number format

## 📱 Responsive Testing

Test on different screen sizes:
1. Open Chrome DevTools (F12)
2. Click device toolbar icon (Ctrl+Shift+M)
3. Try different devices:
   - iPhone SE (375px)
   - iPad (768px)
   - Desktop (1920px)

## ✅ Development Checklist

- [ ] Dependencies installed (`npm install`)
- [ ] Environment configured (`.env` file)
- [ ] Dev server running (`npm run dev`)
- [ ] App accessible at `http://localhost:5173`
- [ ] Hero section displays correctly
- [ ] Dispute form validates inputs
- [ ] Status tracker can search disputes
- [ ] Responsive on mobile/tablet/desktop
- [ ] Dark mode works

## 🎓 Learning Resources

- **React**: https://react.dev/learn
- **TypeScript**: https://www.typescriptlang.org/docs/
- **Tailwind CSS**: https://tailwindcss.com/docs
- **shadcn/ui**: https://ui.shadcn.com/docs
- **Vite**: https://vite.dev/guide/

## 🚀 Next Steps

1. **Integrate with Backend**: Connect to your Java Spring Boot API
2. **Add Authentication**: Implement OTP-based phone auth (optional)
3. **Add Analytics**: Track form submissions and errors
4. **Performance**: Optimize images and lazy load components
5. **Testing**: Add unit tests with Vitest

---

Need help? Check FRONTEND_README.md for detailed documentation!
