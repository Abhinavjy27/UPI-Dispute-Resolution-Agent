# PERSON B1 - Frontend & UI Development

## 👤 Role Overview

**Primary Responsibilities:**

- React frontend application development
- Responsive UI design with Tailwind CSS
- Form validation and user experience
- API integration with backend
- Component architecture and state management
- Performance optimization

**Time Allocation (12 hours):**
- Setup & Project Structure: 1 hour
- Form Components: 2 hours
- Status & Tracking Pages: 2 hours
- Styling & Responsiveness: 1.5 hours
- API Integration: 1.5 hours
- Testing & Edge Cases: 2 hours
- Polish & Performance: 1.5 hours

---

## 📋 Detailed Tasks

### **Phase 1: Setup & Project Structure (1 hour)**

**Tasks:**
- [ ] Create Vite React project
- [ ] Install dependencies (axios, recharts, tailwindcss)
- [ ] Set up Tailwind CSS configuration
- [ ] Create folder structure for components
- [ ] Set up environment variables (.env.local)
- [ ] Configure API base URL
- [ ] Set up routing (React Router or simple conditional rendering)

**Folder Structure to Create:**
```
frontend/
├── src/
│   ├── components/
│   │   ├── DisputeForm.jsx
│   │   ├── StatusPage.jsx
│   │   ├── Header.jsx
│   │   ├── Footer.jsx
│   │   ├── LoadingSpinner.jsx
│   │   └── ErrorAlert.jsx
│   ├── pages/
│   │   ├── HomePage.jsx
│   │   └── StatusPage.jsx
│   ├── styles/
│   │   └── globals.css
│   ├── App.jsx
│   ├── main.jsx
│   └── api.js (axios instance)
├── public/
├── index.html
├── tailwind.config.js
├── postcss.config.js
└── vite.config.js
```

**Checklist:**
- [ ] `npm create vite@latest . -- --template react`
- [ ] Install all dependencies
- [ ] Tailwind CSS initialized
- [ ] .env.local created with VITE_API_URL
- [ ] Folder structure created
- [ ] npm run dev starts without errors

---

### **Phase 2: Form Components (2 hours)**

#### **2.1: Dispute Form Component (1.5 hours)**

**File: `src/components/DisputeForm.jsx`**

**Features to Implement:**

1. **Form Input Fields:**
   - [ ] Transaction ID input with pattern validation
   - [ ] Merchant UPI input with format checking
   - [ ] Amount input (₹1 - ₹100,000)
   - [ ] Customer phone input (+91 format)
   - [ ] Real-time validation with error messages

2. **Form State Management:**
   ```javascript
   const [formData, setFormData] = useState({
     transactionId: '',
     merchantUpi: '',
     amount: '',
     customerPhone: '+91'
   });

   const [errors, setErrors] = useState({});
   const [loading, setLoading] = useState(false);
   ```

3. **Validation Logic:**
   ```javascript
   ✓ Transaction ID: TXN followed by 14+ digits
   ✓ Merchant UPI: xxx@bank format
   ✓ Amount: 1-100,000 only
   ✓ Phone: +91 followed by 10 digits
   ✓ All fields required
   ```

4. **UI Elements:**
   - [ ] Clean form layout with proper spacing
   - [ ] Input field labels with help text
   - [ ] Real-time error display (red text below field)
   - [ ] Disabled submit button until valid
   - [ ] Loading state during submission
   - [ ] Spinner animation on submit button

5. **API Call on Submit:**
   ```javascript
   POST http://localhost:8000/api/disputes
   {
     transactionId: "TXN20260227123456",
     merchantUpi: "amazon@upi",
     amount: 5000,
     customerPhone: "+919876543210"
   }
   ```

6. **Success/Error Handling:**
   - [ ] Show loading spinner during API call
   - [ ] On success: Redirect to status page
   - [ ] On error: Show error message with details
   - [ ] Clear errors when user fixes input

**Code Template:** See [06_IMPLEMENTATION_GUIDE.md](../06_IMPLEMENTATION_GUIDE.md)

**Checklist:**
- [ ] Form renders correctly
- [ ] All fields accept input
- [ ] Real-time validation works
- [ ] Submit button disabled until form valid
- [ ] Loading spinner shows during request
- [ ] Error messages clear and helpful
- [ ] Keyboard navigation works (Tab)
- [ ] Mobile view works (100% width)

#### **2.2: Additional Form Components (30 min)**

**File: `src/components/Header.jsx`**
- [ ] App title and tagline
- [ ] Optional: Navigation menu
- [ ] Styling: Blue gradient background

**File: `src/components/ErrorAlert.jsx`**
- [ ] Reusable error display component
- [ ] Shows error message with icon
- [ ] Optional: Close button to dismiss
- [ ] Styling: Red background with border

**File: `src/components/LoadingSpinner.jsx`**
- [ ] Animated spinner while loading
- [ ] Shows loading message
- [ ] Styling: Centered with animation

---

### **Phase 3: Status & Tracking Pages (2 hours)**

#### **3.1: Status Page Component (1.5 hours)**

**File: `src/components/StatusPage.jsx`**

**Features:**

1. **Status Display:**
   - [ ] Show dispute ID (large, easy to copy)
   - [ ] Show status badge with color
     ```
     ✅ REFUND_INITIATED (green)
     ❌ REJECTED (red)
     ⏳ MANUAL_REVIEW (yellow)
     ```
   - [ ] Show main message (large text, clear)

2. **Dispute Details Section:**
   ```
   ├─ Dispute ID: DIS_1709028600
   ├─ Transaction ID: TXN20260227123456
   ├─ Amount: ₹5,000
   ├─ Merchant UPI: amazon@upi
   ├─ Created: Feb 27, 2:30 PM
   └─ Status: REFUND_INITIATED
   ```

3. **NEFT Refund Details** (if refund initiated):
   - [ ] NEFT Reference number (large, copyable)
   - [ ] Expected settlement date/time
   - [ ] Copy to clipboard button
   - [ ] Success message about timeline

4. **Timeline View:**
   ```
   ✓ Failure verified (timestamp)
   ✓ Refund auto-approved (timestamp)
   ⏳ NEFT processing (timestamp)
   ⏳ Money arriving (24-48h)
   ```

5. **Action Buttons:**
   - [ ] "File Another Dispute" → back to form
   - [ ] "Copy Dispute ID" → clipboard
   - [ ] "Copy NEFT Ref" → clipboard (if present)
   - [ ] "Download Receipt" (optional)

**Checklist:**
- [ ] Status page renders correctly
- [ ] All data displays properly
- [ ] Copy to clipboard works
- [ ] Buttons functional
- [ ] Colors match status
- [ ] Timeline is clear
- [ ] Responsive on mobile

#### **3.2: Status Checker Component (30 min)**

**File: `src/components/StatusChecker.jsx`**

**Features:**
- [ ] Input field for dispute ID
- [ ] Button to fetch status
- [ ] Display current status
- [ ] Handle "not found" error
- [ ] Auto-refresh option (optional)

---

### **Phase 4: Styling & Responsiveness (1.5 hours)**

#### **4.1: Tailwind CSS Configuration**

**Tasks:**
- [ ] Configure `tailwind.config.js` with custom colors
- [ ] Set up color scheme for UPI dispute system
- [ ] Configure spacing, typography
- [ ] Add custom utilities if needed

**Color Scheme:**
```css
Primary: Indigo (#4F46E5)
Success: Green (#10B981)
Error: Red (#EF4444)
Warning: Yellow (#F59E0B)
Info: Blue (#3B82F6)
Background: Light gray (#F9FAFB)
```

#### **4.2: Global Styles**

**File: `src/styles/globals.css`**

**Content:**
```css
@tailwind base;
@tailwind components;
@tailwind utilities;

/* Custom animations */
@keyframes spin { ... }
@keyframes fadeIn { ... }

/* Custom components */
.btn-primary { ... }
.card { ... }
.badge-success { ... }
```

#### **4.3: Responsive Design**

**Tasks:**
- [ ] Mobile-first design approach
- [ ] Test on mobile (DevTools, actual device)
- [ ] Breakpoints:
  ```
  sm: 640px  (mobile)
  md: 768px  (tablet)
  lg: 1024px (desktop)
  ```
- [ ] Form inputs full width on mobile
- [ ] Font sizes scale appropriately
- [ ] Touch-friendly buttons (min 44px)

**Checklist:**
- [ ] Form looks good on phone (320px)
- [ ] Form looks good on tablet (768px)
- [ ] Form looks good on desktop (1024px)
- [ ] No horizontal scrolling
- [ ] All buttons clickable on touch
- [ ] Text readable at all sizes

---

### **Phase 5: API Integration (1.5 hours)**

#### **5.1: Axios Configuration**

**File: `src/api.js`**

```javascript
import axios from 'axios';

const API_URL = process.env.VITE_API_URL || 'http://localhost:8000';

const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const disputeAPI = {
  fileDispute: (data) => api.post('/api/disputes', data),
  getStatus: (disputeId) => api.get(`/api/disputes/${disputeId}`),
  health: () => api.get('/api/health')
};
```

#### **5.2: API Error Handling**

**Tasks:**
- [ ] Handle network errors (no internet)
- [ ] Handle timeout errors (server slow)
- [ ] Handle validation errors (400)
- [ ] Handle not found errors (404)
- [ ] Handle server errors (500)
- [ ] Show user-friendly error messages
- [ ] Retry mechanism (optional)

**Error Messages:**
```javascript
{
  NETWORK_ERROR: "Network error. Check your internet.",
  TIMEOUT: "Request timed out. Server slow.",
  VALIDATION: "Please check your input.",
  NOT_FOUND: "Dispute not found.",
  SERVER_ERROR: "Server error. Try again later."
}
```

#### **5.3: State Management**

**Tasks:**
- [ ] Use React Context or useState for global state
- [ ] Store dispute data after filing
- [ ] Store form state
- [ ] Handle loading states
- [ ] Handle error states
- [ ] Persist data in localStorage (optional)

**Checklist:**
- [ ] Form submits successfully to backend
- [ ] Response received and parsed correctly
- [ ] Status page displays response data
- [ ] Error messages shown on failure
- [ ] Loading spinner shows during request
- [ ] No CORS errors from frontend

---

### **Phase 6: Testing & Edge Cases (2 hours)**

#### **6.1: Manual Testing**

**Test Scenarios:**

1. **Valid Input:**
   - [ ] File dispute with correct data
   - [ ] See success message
   - [ ] See status page
   - [ ] Dispute ID and NEFT reference visible

2. **Invalid Input:**
   - [ ] Missing field → error message
   - [ ] Wrong format → validation error
   - [ ] Amount too high → error message
   - [ ] Wrong phone format → error message

3. **API Scenarios:**
   - [ ] Network offline → show error
   - [ ] Backend down → show error
   - [ ] Slow response → loading spinner
   - [ ] Duplicate transaction → error from backend

4. **UI Interactions:**
   - [ ] Copy to clipboard works
   - [ ] Go back to form button works
   - [ ] Form resets after submission
   - [ ] Buttons disabled during loading

5. **Mobile Testing:**
   - [ ] Form fits on small screen
   - [ ] Keyboard appears on input focus
   - [ ] Buttons large enough to tap
   - [ ] No horizontal scrolling

**Test Cases Checklist:**
- [ ] Happy path (valid input → success)
- [ ] Validation errors (invalid input)
- [ ] Network errors (offline)
- [ ] Server errors (500)
- [ ] Duplicate transaction error
- [ ] Slow network (loading state)

#### **6.2: Browser Testing**

- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Safari (if on Mac)
- [ ] Edge (if on Windows)
- [ ] Mobile browsers (iOS & Android)

**Checklist:**
- [ ] No console errors
- [ ] No warnings
- [ ] Responsive on all browsers
- [ ] Form works on all browsers
- [ ] API calls working

---

### **Phase 7: Polish & Performance (1.5 hours)**

#### **7.1: UI Polish**

**Tasks:**
- [ ] Review all fonts, sizes, colors
- [ ] Consistent spacing throughout
- [ ] Button hover states
- [ ] Input focus states
- [ ] Smooth transitions/animations
- [ ] Remove debug console.logs
- [ ] Fix any visual bugs

**Checklist:**
- [ ] App looks professional ✓
- [ ] Colors consistent ✓
- [ ] Typography hierarchy clear ✓
- [ ] Spacing balanced ✓
- [ ] Animations smooth ✓
- [ ] No visual glitches ✓

#### **7.2: Performance Optimization**

**Tasks:**
- [ ] Lazy load components (optional)
- [ ] Optimize images
- [ ] Minimize bundle size
- [ ] Remove unused CSS
- [ ] Enable gzip compression
- [ ] Check Lighthouse score target: 90+

**Commands:**
```bash
npm run build  # Check build size
npm run preview  # Test production build
```

#### **7.3: Code Quality**

**Tasks:**
- [ ] Remove console.logs
- [ ] Format code (Prettier)
- [ ] Check for linting errors (ESLint)
- [ ] Add comments for complex logic
- [ ] Consistent naming conventions
- [ ] No dead code

---

## 🎯 Success Criteria

**By End of 12 Hours:**

- ✅ React app starts with `npm run dev`
- ✅ Form renders with proper validation
- ✅ Form submits to backend successfully
- ✅ Status page displays data correctly
- ✅ Copy to clipboard works
- ✅ Error messages show on failure
- ✅ Loading spinner shows during API calls
- ✅ Application is responsive (mobile/tablet/desktop)
- ✅ No CORS errors
- ✅ All animations smooth
- ✅ Code is clean and readable
- ✅ Performance is good (Lighthouse 90+)

---

## 📞 Communication with Team

| Time | Status | Notes |
|------|--------|-------|
| Hour 1 | ✓ Setup | React project initialized, dependencies installed |
| Hour 2 | ✓ Form component | DisputeForm working with validation |
| Hour 3 | ✓ More forms | Header, Error, Spinner components done |
| Hour 4 | ✓ Status page | StatusPage component displaying data |
| Hour 5 | ✓ Styling | Tailwind CSS applied, responsive design done |
| Hour 6 | ✓ API integration | Form submitting to backend successfully |
| Hour 8 | ✓ Testing | All edge cases tested, no errors |
| Hour 9 | ✓ Polish | UI refined, animations smooth |
| Hour 12 | ✅ DONE | Frontend ready for demo |

---

## 🛠️ Technologies Used

```
Framework:  React 18
Build:      Vite
Styling:    Tailwind CSS
HTTP:       Axios
State:      React Hooks (useState, useContext)
Testing:    Manual + Browser DevTools
Deployment: Vercel (later)
```

---

## 📚 Reference Documents

- [04_API_CONTRACTS.md](../04_API_CONTRACTS.md) - API endpoints
- [05_TECH_STACK_AND_SETUP.md](../05_TECH_STACK_AND_SETUP.md) - Setup guide
- [06_IMPLEMENTATION_GUIDE.md](../06_IMPLEMENTATION_GUIDE.md) - Code examples

**Let's build an amazing UI! 🎨**
