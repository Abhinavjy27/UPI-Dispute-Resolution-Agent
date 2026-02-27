// Simple Mock Bank API for UPI Dispute Resolution
// Port: 9000
// Usage: node mock-bank-api.js

const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

// Single unified mock endpoint
app.post('/mock-bank/verify', (req, res) => {
  const { type, transactionId, accountNumber, merchantUpi, amount } = req.body;
  
  console.log(`[Mock Bank] Verifying ${type} for ${transactionId}, amount: ₹${amount}`);
  
  // Simulate processing delay (realistic)
  setTimeout(() => {
    if (type === 'CUSTOMER') {
      // Customer bank - money WAS debited
      return res.json({
        success: true,
        verified: true,
        accountNumber: accountNumber,
        transactionId: transactionId,
        debited: true,
        amount: amount,
        debitTimestamp: new Date().toISOString(),
        status: 'CONFIRMED'
      });
    } 
    else if (type === 'MERCHANT') {
      // Merchant bank - money was NOT credited (failure scenario)
      return res.json({
        success: true,
        verified: true,
        merchantUpi: merchantUpi,
        transactionId: transactionId,
        credited: false,
        amount: 0,
        status: 'TRANSACTION_NOT_FOUND',
        errorCode: 'TXN_NOT_RECEIVED'
      });
    } 
    else {
      return res.status(400).json({
        success: false,
        error: 'Invalid type. Must be CUSTOMER or MERCHANT'
      });
    }
  }, 1000); // 1 second delay to simulate real API
});

// Alternative: Separate endpoints (if needed)
app.post('/customer-bank/verify', (req, res) => {
  const { transactionId, accountNumber, amount } = req.body;
  
  setTimeout(() => {
    res.json({
      success: true,
      verified: true,
      accountNumber: accountNumber,
      transactionId: transactionId,
      debited: true,
      amount: amount,
      debitTimestamp: new Date().toISOString()
    });
  }, 800);
});

app.post('/merchant-bank/verify', (req, res) => {
  const { transactionId, merchantUpi, amount } = req.body;
  
  setTimeout(() => {
    res.json({
      success: true,
      verified: true,
      merchantUpi: merchantUpi,
      transactionId: transactionId,
      credited: false,
      amount: 0,
      status: 'TRANSACTION_NOT_FOUND'
    });
  }, 900);
});

// Health check
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    service: 'Mock Bank API',
    timestamp: new Date().toISOString()
  });
});

const PORT = process.env.PORT || 9000;
app.listen(PORT, () => {
  console.log(`✅ Mock Bank API running on http://localhost:${PORT}`);
  console.log(`\nEndpoints:`);
  console.log(`  POST /mock-bank/verify (unified)`);
  console.log(`  POST /customer-bank/verify`);
  console.log(`  POST /merchant-bank/verify`);
  console.log(`  GET  /health\n`);
});
