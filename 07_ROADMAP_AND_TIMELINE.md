# 07_ROADMAP_AND_TIMELINE

## ⏱️ 12-Hour Hackathon Timeline

Complete breakdown of how to build, test, and demo in exactly 12 hours.

---

## 📋 Hour-by-Hour Breakdown

### **Hours 0-1: Setup (Team Sync + Environment)**

**Team Setup (15 min)**
```
├─ Assign roles:
│  ├─ Backend lead: Disputes API
│  ├─ Frontend lead: Form + Status UI
│  └─ Integration: Testing + Demo
│
├─ Share this document with whole team
├─ Git setup (if not done): Create GitHub repo, add team
└─ Slack/Discord channel for async updates
```

**Environment Setup (30 min)**
```bash
# Backend developer
cd backend
python3.11 -m venv venv
source venv/bin/activate
pip install fastapi uvicorn sqlalchemy pydantic

# Frontend developer
cd frontend
npm create vite@latest . -- --template react
npm install axios recharts tailwindcss

# Both
├─ Test backend: uvicorn main:app --reload --port 8000
├─ Test frontend: npm run dev
└─ Verify both start without errors
```

**Verify Setup (15 min)**
```
├─ [ ] Backend runs on http://localhost:8000
├─ [ ] Frontend runs on http://localhost:5173
├─ [ ] Can access FastAPI docs: http://localhost:8000/docs
└─ [ ] All packages installed (check with pip list / npm list)
```

**Status:** ✅ Environment ready, team aligned

---

### **Hours 1-3: Core Backend (Disputes API)**

**Backend Folder Structure (10 min)**
```bash
mkdir -p backend/models backend/routes backend/services
touch backend/db.py backend/main.py backend/.env
touch backend/models/dispute.py
touch backend/routes/disputes.py
touch backend/services/bank_api.py
```

**Copy Code Files (from [06_IMPLEMENTATION_GUIDE.md](06_IMPLEMENTATION_GUIDE.md))**

```
File: backend/db.py ..................... Copy db.py section
File: backend/models/dispute.py ......... Copy dispute.py section
File: backend/routes/disputes.py ........ Copy disputes.py section
File: backend/services/bank_api.py ...... Copy bank_api.py section (MOCKED)
File: backend/main.py ................... Copy main.py section
```

**Time: 60-90 minutes**

**Test Backend**

```bash
cd backend
source venv/bin/activate
python -m uvicorn main:app --reload --port 8000

# Check:
# 1. Server starts without errors
# 2. Can access http://localhost:8000/docs
# 3. Database file created: disputes.db

# Test endpoint:
curl http://localhost:8000/api/disputes -X POST \
  -H "Content-Type: application/json" \
  -d '{"transaction_id":"TXN20260227123456","merchant_upi":"amazon@upi","amount":5000,"customer_phone":"+919876543210"}'

# Expected response:
# {"success": true, "dispute_id": "DIS_...", "status": "REFUND_INITIATED", ...}
```

**Status:** ✅ Backend working, API responds correctly

---

### **Hours 3-5: Core Frontend (Form + Status)**

**Frontend Folder Structure (10 min)**
```bash
mkdir -p frontend/src/components frontend/src/styles
touch frontend/src/components/DisputeForm.jsx
touch frontend/src/components/StatusPage.jsx
touch frontend/src/styles/globals.css
touch frontend/src/App.jsx
```

**Copy Code Files (from [06_IMPLEMENTATION_GUIDE.md](06_IMPLEMENTATION_GUIDE.md))**

```
File: frontend/src/App.jsx ........................... Copy App.jsx
File: frontend/src/components/DisputeForm.jsx ....... Copy DisputeForm.jsx
File: frontend/src/components/StatusPage.jsx ........ Copy StatusPage.jsx
File: frontend/src/styles/globals.css .............. Copy globals.css
```

**Configure Tailwind**

```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p

# Edit tailwind.config.js (copy from [05_TECH_STACK_AND_SETUP.md](05_TECH_STACK_AND_SETUP.md))
# Edit frontend/src/main.jsx: import './styles/globals.css'
```

**Test Frontend**

```bash
cd frontend
npm run dev

# Check:
# 1. App loads on http://localhost:5173/
# 2. App shows form with 4 input fields
# 3. Form has proper styling (Tailwind CSS applied)
# 4. Button says "Submit Dispute"

# Test interaction:
# 1. Type in fields
# 2. Click button
# 3. Should show validation errors if invalid input
```

**Status:** ✅ Frontend working, form renders

---

### **Hours 5-6: Integration Testing (Frontend ↔ Backend)**

**Connect Frontend to Backend (30 min)**

```javascript
// In DisputeForm.jsx, verify API URL
const response = await axios.post(
  'http://localhost:8000/api/disputes',  // ← Check this URL
  { ... }
);
```

**Test Full Flow**

```
1. Start both servers (2 terminals):
   Terminal 1: cd backend && uvicorn main:app --reload --port 8000
   Terminal 2: cd frontend && npm run dev

2. Open http://localhost:5173/

3. Fill form:
   - Transaction ID: TXN20260227123456
   - Merchant UPI: amazon@upi
   - Amount: 5000
   - Phone: +919876543210

4. Click Submit

5. Expected:
   ✓ Loading state shows ("Verifying...")
   ✓ Backend processes request (check backend logs)
   ✓ Page changes to status page after 5-10 seconds
   ✓ Shows "✅ Refund Initiated!"
   ✓ Displays dispute_id and neft_reference
   ✓ Message: "Money by tomorrow 9 AM"

6. If CORS error:
   ✓ Restart backend
   ✓ Check CORS middleware in main.py
```

**Test Error Handling**

```
Try these inputs (should show errors):

Test 1: Invalid transaction ID
├─ Input: TXN123 (too short)
├─ Expected: Error message in red

Test 2: Invalid UPI
├─ Input: notaupi (no @ sign)
├─ Expected: Error message

Test 3: Invalid amount
├─ Input: 999999 (too high)
├─ Expected: Error message

Test 4: Invalid phone
├─ Input: 1234567890 (no +91)
├─ Expected: Error message

Test 5: False claim (2nd submission triggers 95% random path)
├─ If lucky: Shows "Money already reached merchant"
├─ Expected: Status = REJECTED
```

**Status:** ✅ Full flow working end-to-end

---

### **Hours 6-7: Polish & Testing**

**Backend Polish (15 min)**

```
├─ [ ] Check all error messages are clear
├─ [ ] Test network timeout (should escalate to manual review)
├─ [ ] Test duplicate submission (should reject)
├─ [ ] Check database records are saved
├─ [ ] Verify timestamps are correct
└─ [ ] Response times < 10 seconds
```

**Frontend Polish (15 min)**

```
├─ [ ] Form validation happens in real-time
├─ [ ] Error messages disappear when you fix input
├─ [ ] Success page is clear and readable
├─ [ ] Can copy dispute ID to clipboard
├─ [ ] Can go back to form from success page
├─ [ ] Mobile view looks good (use browser DevTools)
├─ [ ] No console errors (check DevTools → Console)
└─ [ ] Loading spinner shows during processing
```

**Create Test Data (15 min)**

```
Prepare these test cases for demo:

Test Case 1 (Success - Real failure):
├─ TXN20260227001111
├─ amazon@upi
├─ ₹5,000
└─ +919876543210
└─ Expected: Refund approved, NEFT started

Test Case 2 (Failed - Money reached):
├─ TXN20260227002222
├─ flipkart@upi
├─ ₹3,500
└─ +919876547890
└─ Expected: Claim rejected (money already received)

Test Case 3 (Edge - Manual review):
├─ TXN20260227003333
├─ ola@upi
├─ ₹2,000
└─ +919876542222
└─ Expected: Unclear case, needs manual review
```

**Status:** ✅ UI polished, test data ready

---

### **Hours 7-9: Optional Features** (If time permits)

**Priority 1: Risk Score (2 hours)**

If you have time and want to impress judges:

```
What to add:
├─ New endpoint: POST /api/risk-score
├─ New component: RiskScore.jsx
├─ Display on form before submit
│  ├─ If HIGH risk: Show warning
│  └─ Suggest alternatives
│
Implementation:
├─ Copy risk_scorer.py from [03_PRODUCT_SPECIFICATION.md](03_PRODUCT_SPECIFICATION.md)
├─ Add endpoint to main.py
├─ Add UI component to form
└─ Call before user submits

Time breakdown:
├─ Backend: 30 min (model + endpoint)
├─ Frontend: 30 min (component + styling)
├─ Testing: 30 min (verify suggestions work)
└─ Remaining: 30 min buffer
```

**Priority 2: Merchant Dashboard (1 hour)**

If you still have time:

```
What to add:
├─ New page: /dashboard
├─ Simple charts showing:
│  ├─ Disputes over time
│  ├─ Disputes by merchant
│  └─ Health status
│
Implementation:
├─ Use Recharts (already installed)
├─ Query disputes from DB
├─ Render charts
└─ Add link to dashboard from main app

Time: 30 min backend + 30 min frontend
```

**If NO Time for Optional:**
```
That's FINE! Skip optional features and focus on Polish + Demo.
Judges care more about working core than broken advanced features.
```

**Status:** ✅ Optional features completed (if time)

---

### **Hours 9-11: Demo Preparation**

**Demo Script (30 min)**

Write this down, practice it:

```
[Time: 0-60 seconds]

"Today we're solving a ₹10 billion problem in India:
Failed UPI transactions. Customer sends money, it leaves 
their account, but never reaches the merchant. Traditional 
banks take 5-7 days to investigate.

We solve it in 24 hours with automation.

[Go to browser, show app]

Here's how it works:

[Live demo of successful case]

1. Customer files dispute (amount, merchant, transaction ID)
   └─ Click Submit
   
   [Wait 5-10 seconds...]
   
2. Our system instantly calls both banks:
   ├─ Customer bank: 'Was ₹5000 debited?' → YES ✓
   └─ Merchant bank: 'Did you receive it?' → NO ✗
   
3. Clear failure detected! Auto-approve refund
   └─ NEFT reference: NEFT20260227123456
   └─ Money arrives tomorrow at 9 AM
   
[Show success screen]

Impact: 7x faster (24h vs 5-7 days), 91% cheaper (₹50 vs ₹500),
95% automated.

[If time permits, show risk score feature]

Questions?"
```

**Practice Demo (20 min)**

```
1. Do the demo 3 times without looking at notes
2. Time yourself (should be 60-90 seconds)
3. Practice with both test cases
4. Have backup wifi/internet (in case 4G fails)
5. Save this script in Notes app on your phone
```

**Demo Checklist (20 min)**

```
Before presenting:

Setup:
├─ [ ] Both servers running (backend on 8000, frontend on 5173)
├─ [ ] Browser zoomed to readable size (140% or 150%)
├─ [ ] Have test data ready (copy-paste ready)
├─ [ ] Phone on silent
├─ [ ] Have backup browser tab open
└─ [ ] DevTools closed (F12)

Demo:
├─ [ ] Open app: http://localhost:5173/
├─ [ ] Test Case 1 ready to paste
├─ [ ] Can show database (optional: open disputes.db)
├─ [ ] Can show API response (GET request to check status)
└─ [ ] Have 3 screenshots ready (just in case)

Presentation:
├─ [ ] Know your 60-second pitch
├─ [ ] Can explain the problem clearly
├─ [ ] Can demonstrate the solution
├─ [ ] Can answer "Why not just use X?"
└─ [ ] Can discuss architecture choices
```

**Status:** ✅ Demo ready, presentation practiced

---

### **Hour 11-12: Final Polish & Answers**

**Last-Minute Fixes (30 min)**

```
If anything is broken:

├─ Form not submitting?
│  └─ Check backend logs, verify CORS
│
├─ Frontend not loading?
│  └─ npm start again, clear cache
│
├─ Database error?
│  └─ Delete disputes.db, restart backend
│
├─ Status not updating?
│  └─ Check GET endpoint works
│
└─ Styling looks off?
   └─ Ctrl+Shift+R to hard refresh browser
```

**Prepare Answers to Common Questions (20 min)**

```
Q: "What if bank API is down?"
A: "We escalate to manual review. Our team investigates within 24 hours.
   Failover: Queue the request and retry every 5 minutes."

Q: "How do you handle fraud?"
A: "We verify with both banks. If only one confirms, we escalate to 
   manual review. Risk score helps detect suspicious patterns."

Q: "How do you scale this?"
A: "Move from SQLite to PostgreSQL, add async queues (Celery), 
   load balancing (Nginx), and caching (Redis). Current design 
   handles 10k disputes/minute easily."

Q: "Why not X technology instead of Y?"
A: "Fast setup (minutes vs hours), production-grade (real companies use),
   good documentation, and easy debugging for hackathon."

Q: "How's the business model?"
A: "B2B2C. Finteches/banks pay ₹45 per dispute (vs ₹500 they charge).
   Scale: Millions daily. Margins: 80%+."

Prep answers you're comfortable with!
```

**Final Walkthrough (10 min)**

```
Do ONE final clean run:

1. Kill all existing processes
2. Start fresh backend & frontend
3. Do the demo once, end-to-end
4. Check for any errors
5. Fix if needed
6. Take a break, drink water
7. Get ready to present!
```

**Status:** ✅ Ready to present!

---

## 🎯 Success Metrics

### **MVP Must Haves (to pass)**

```
✅ Form accepts 4 inputs
✅ Form validates input formats
✅ Backend receives request
✅ Bank verification happens (simulated)
✅ Correct decision is made (verified failure)
✅ Status page shows result
✅ User sees dispute_id and neft_reference
✅ No critical errors or crashes
```

### **Good Implementation (to impress)**

```
✅ Responsive design (works on mobile)
✅ Clear error messages
✅ Proper HTTP status codes
✅ Database transactions work
✅ Code is clean and readable
✅ Proper API documentation
✅ Test data works for multiple cases
```

### **Great Implementation (to win)**

```
✅ Risk score feature implemented
✅ Merchant dashboard with charts
✅ Root cause analysis
✅ Proper logging and monitoring
✅ Clean git history
✅ README with clear instructions
✅ Polished UI with consistent branding
```

---

## 📊 Timeline Summary

```
Hours 0-1:  [████░░░░░░░░░░░░░░░░░░] Setup
Hours 1-3:  [████████░░░░░░░░░░░░░░] Backend
Hours 3-5:  [████████████░░░░░░░░░░] Frontend
Hours 5-6:  [██ ] Integration Testing
Hours 6-7:  [██░░░░░░░░░░░░░░░░░░] Polish
Hours 7-9:  [██░░░░░░░░░░░░░░░░░░] Optional (if time)
Hours 9-11: [██████░░░░░░░░░░░░░░] Demo Prep
Hours 11-12:[██░░░░░░░░░░░░░░░░░░] Final Polish

Core System: ✅ 7 hours
Optional: ⏳ 1-2 hours  
Demo + Polish: ✅ 3-4 hours
```

---

## 🎁 Bonus: Post-Hackathon Roadmap

### **Week 1: Production Hardening**

```
├─ [ ] Real bank API integration (replace mocks)
├─ [ ] User authentication (JWT tokens)
├─ [ ] Rate limiting on endpoints
├─ [ ] Proper error logging (Sentry)
├─ [ ] Database migrations (Alembic)
└─ [ ] HTTPS setup (SSL certificates)
```

### **Week 2: Advanced Features**

```
├─ [ ] Merchant Health Dashboard (full version)
├─ [ ] Machine learning risk model
├─ [ ] SMS/Email notifications (Twilio/SendGrid)
├─ [ ] Admin portal for manual reviews
├─ [ ] Analytics dashboard
└─ [ ] Webhook integrations
```

### **Week 3: Deployment**

```
├─ [ ] Frontend: Deploy to Vercel
├─ [ ] Backend: Deploy to Railway/Render
├─ [ ] Database: PostgreSQL on cloud
├─ [ ] CI/CD pipeline (GitHub Actions)
├─ [ ] Monitoring (DataDog/New Relic)
└─ [ ] Load testing & optimization
```

### **Week 4: Go-to-Market**

```
├─ [ ] Security audit (penetration testing)
├─ [ ] Compliance check (RBI, NPCI)
├─ [ ] User testing with 100 real UPI failures
├─ [ ] Marketing website
├─ [ ] Pitch to investors
└─ [ ] First customer pilots
```

---

## 💡 Last Minute Tips

```
1. Don't Overthink
   ├─ Simple usually wins
   └─ Done > Perfect

2. Test Early, Test Often
   ├─ Test after every 30 minutes of code
   └─ Catch bugs early before they compound

3. Communicate with Team
   ├─ Update on progress every hour
   ├─ Flag blockers immediately
   └─ Help teammates if they fall behind

4. Sleep is Overrated, Coffee is Life ☕
   ├─ 🕐 Hour 3-4: First coffee
   ├─ 🕐 Hour 6-7: Second coffee
   ├─ 🕐 Hour 9-10: Energy drink (if surviving)
   └─ 🕐 Hour 11-12: VICTORY COFFEE (no sleep needed)

5. If Stuck
   ├─ Ask on Slack before debugging alone
   ├─ Use ChatGPT for syntax questions
   ├─ Restart servers (fixes 50% of issues)
   ├─ Clear cache and rebuild
   └─ If all else fails: comment out the code and move on

6. Demo Day Mindset
   ├─ Users don't see your code, they see YOUR demo
   ├─ A 100% working core beats 50% working everything
   ├─ Confidence > Perfection
   ├─ Practice your pitch until you dream it
   └─ Have FUN! This is supposed to be exciting!
```

---

## 🎉 You've Got This!

```
Remember:
✅ 12 hours is a LOT of time
✅ You have all the code templates ready
✅ Copy-paste is your friend
✅ Demo > Code Quality for hackathons
✅ Ask for help when stuck
✅ Sleep after winning (you'll sleep better!)

Timeline is achievable if you:
1. Don't skip setup
2. Work in parallel (backend + frontend)
3. Test as you go
4. Skip optional features if needed
5. Practice demo 3x

Good luck! 🚀

See you at the finish line! 🏁
```

---

## 📞 Emergency Contacts

```
If stuck on:
├─ Backend/FastAPI: Check [06_IMPLEMENTATION_GUIDE.md](06_IMPLEMENTATION_GUIDE.md)
├─ Frontend/React: Check DisputeForm.jsx code
├─ Database: Check db.py setup
├─ Integration: Check CORS in main.py
├─ Styling: Check Tailwind CSS imports
├─ Deployment: Skip (for post-hackathon)
└─ Morale: Drink water and take 5-min break
```

**Final message:** You're building something real that solves a real problem.
That's what hackathons are about. Now go build it! 💪

🚀 **LET'S GOOOOO!** 🚀
