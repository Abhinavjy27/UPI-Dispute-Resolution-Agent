# PERSON B2 - ML & Risk Scoring + Advanced Features

## 👤 Role Overview

**Primary Responsibilities:**
- Machine Learning model development for risk scoring
- Risk score calculation algorithm implementation
- Backend ML service integration
- Optional features (Merchant dashboard, Risk analytics)
- Data collection and model training
- Performance optimization of ML pipeline

**Time Allocation (12 hours):**
- Setup & Data Collection: 1 hour
- Risk Scoring Algorithm: 2 hours
- ML Model Development: 3 hours
- Backend Integration: 2 hours
- Optional Features: 2 hours
- Testing & Optimization: 1 hour
- Deployment & Documentation: 1 hour

---

## 📋 Detailed Tasks

### **Phase 1: Setup & Data Collection (1 hour)**

**Tasks:**
- [ ] Set up Python environment (virtual env)
- [ ] Install ML dependencies
- [ ] Create ml/ folder structure
- [ ] Set up data collection pipeline
- [ ] Prepare training dataset format
- [ ] Configure logging

**Dependencies to Install:**
```bash
pip install numpy pandas scikit-learn tensorflow
pip install joblib matplotlib seaborn
pip install requests python-dotenv
```

**Folder Structure:**
```
ml/
├── risk_scorer.py          # Main risk scoring module
├── model_trainer.py        # Model training script
├── data_processor.py       # Data cleaning and preprocessing
├── train_model.py          # Entry point for training
├── evaluate_model.py       # Model evaluation script
├── requirements.txt        # Python dependencies
├── data/
│   ├── raw/               # Raw transaction data
│   ├── processed/         # Cleaned data for training
│   └── test/              # Test dataset
├── models/
│   ├── risk_model.pkl     # Trained model (output)
│   ├── scaler.pkl         # Feature scaler (output)
│   └── feature_names.pkl  # Feature metadata (output)
├── notebooks/
│   └── exploration.ipynb  # Data exploration
└── logs/
    └── training.log       # Training logs
```

**Checklist:**
- [ ] Virtual environment created and activated
- [ ] All dependencies installed
- [ ] Folder structure created
- [ ] requirements.txt generated
- [ ] logging configured
- [ ] Sample dataset prepared

---

### **Phase 2: Risk Scoring Algorithm (2 hours)**

#### **2.1: Algorithm Design**

**Risk Factors (8 total):**

| Factor | Weight | Range | Description |
|--------|--------|-------|-------------|
| Transaction Amount | 15% | ₹1-₹100K | Higher amounts = higher risk |
| Merchant Category | 20% | Various | Some categories higher risk |
| Time of Day | 10% | 0-23h | Late night transactions risky |
| Customer History | 15% | 0-100 | First-time customers risky |
| UPI Network | 10% | Various | Some networks less verified |
| Transaction Frequency | 10% | 0-weekly | Unusual patterns risky |
| Device/Location | 10% | Various | New location = risky |
| Weather/Market | 10% | Various | Market volatility factor |

**Risk Score Formula:**
```
Risk Score = (w1 × amount_score) + (w2 × merchant_score) + ... + (w8 × market_score)
Final Score: 0-100 (0=low, 100=high)
Thresholds:
  0-30    = ✅ LOW RISK → Auto-approve
  30-70   = ⚠️  MEDIUM RISK → Manual review (fast)
  70-100  = ❌ HIGH RISK → Manual review (detailed)
```

#### **2.2: Feature Engineering**

**File: `ml/data_processor.py`**

```python
class FeatureProcessor:
    def __init__(self):
        self.features = []
    
    def engineer_features(self, transaction):
        '''Extract features from transaction data'''
        # Amount-based features
        amount_score = self.score_amount(transaction['amount'])
        
        # Merchant-based features
        merchant_score = self.score_merchant(transaction['merchant_upi'])
        
        # Time-based features
        time_score = self.score_time(transaction['timestamp'])
        
        # Customer-based features (requires history)
        customer_score = self.score_customer_history(
            transaction['customer_phone']
        )
        
        # More features...
        return {
            'amount': amount_score,
            'merchant': merchant_score,
            'time': time_score,
            'customer_history': customer_score,
            # ... more features
        }
```

**Features to Extract:**

1. **Amount Features:**
   - [ ] Absolute amount
   - [ ] Amount percentile (vs average)
   - [ ] Amount velocity (change rate)
   - [ ] Is round amount? (000s)

2. **Merchant Features:**
   - [ ] Merchant category (RTO common?)
   - [ ] Merchant age (days operating)
   - [ ] Merchant dispute rate
   - [ ] Merchant verification score

3. **Time Features:**
   - [ ] Hour of day (0-23)
   - [ ] Day of week (0-6)
   - [ ] Is weekend?
   - [ ] Is off-hours? (11PM-6AM)

4. **Customer Features:**
   - [ ] Customer age
   - [ ] Account age
   - [ ] Transaction count
   - [ ] Dispute history

5. **Behavioral Features:**
   - [ ] Frequency (txns per day)
   - [ ] Recency (days since last txn)
   - [ ] Geographic change
   - [ ] Device change

---

### **Phase 3: ML Model Development (3 hours)**

#### **3.1: Data Preparation**

**File: `ml/model_trainer.py`**

**Tasks:**
- [ ] Load training data (historical transactions)
- [ ] Clean missing values
- [ ] Handle outliers
- [ ] Feature engineering (see Phase 2)
- [ ] Feature scaling (0-1 range)
- [ ] Train/test split (80/20)
- [ ] Stratified split by risk level

```python
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

# Load data
df = pd.read_csv('data/raw/transactions.csv')

# Feature engineering
processor = FeatureProcessor()
X = df.apply(processor.engineer_features, axis=1)
y = df['is_fraudulent']  # Binary target

# Scaling
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Split
X_train, X_test, y_train, y_test = train_test_split(
    X_scaled, y, test_size=0.2, stratify=y, random_state=42
)

# Save scaler for inference
joblib.dump(scaler, 'models/scaler.pkl')
```

#### **3.2: Model Training**

**Algorithms to Try (in priority order):**

1. **Random Forest (Primary)**
   - Good for tabular data
   - Feature importance visibility
   - Fast inference

   ```python
   from sklearn.ensemble import RandomForestClassifier
   
   model = RandomForestClassifier(
       n_estimators=100,
       max_depth=10,
       min_samples_split=5,
       random_state=42,
       n_jobs=-1  # Parallel processing
   )
   model.fit(X_train, y_train)
   ```

2. **Gradient Boosting (Secondary)**
   - Better accuracy than Random Forest
   - Slower training
   - May overfit

   ```python
   from xgboost import XGBClassifier
   
   model = XGBClassifier(
       n_estimators=100,
       max_depth=5,
       learning_rate=0.1,
       random_state=42
   )
   ```

3. **Logistic Regression (Baseline)**
   - Simple, interpretable
   - Fast training & inference
   - Linear relationships only

   ```python
   from sklearn.linear_model import LogisticRegression
   
   model = LogisticRegression(max_iter=1000)
   ```

**Training Script:**
```python
# Train
model.fit(X_train, y_train)

# Evaluate
from sklearn.metrics import (
    accuracy_score, precision_score, 
    recall_score, f1_score, confusion_matrix
)

y_pred = model.predict(X_test)
print(f"Accuracy: {accuracy_score(y_test, y_pred):.3f}")
print(f"Precision: {precision_score(y_test, y_pred):.3f}")
print(f"Recall: {recall_score(y_test, y_pred):.3f}")
print(f"F1: {f1_score(y_test, y_pred):.3f}")
```

#### **3.3: Model Evaluation**

**File: `ml/evaluate_model.py`**

**Metrics to Track:**

| Metric | Target | Why Important |
|--------|--------|---------------|
| Accuracy | >85% | Overall correctness |
| Precision | >80% | False alarm rate |
| Recall | >75% | Catch fraud rate |
| F1 Score | >75% | Balance both |
| AUC-ROC | >0.85 | Rank ordering |

**Confusion Matrix Analysis:**
```
        Predicted
      Fraud  Legit
Actual -------- ----
Fraud   TP|FN
Legit   FP|TN

Optimize for: Low FN (catch fraud) + Low FP (piss off customers)
```

**Cross-Validation:**
```python
from sklearn.model_selection import cross_val_score

scores = cross_val_score(model, X_train, y_train, cv=5)
print(f"Cross-val score: {scores.mean():.3f} ± {scores.std():.3f}")
```

**Feature Importance:**
```python
importances = model.feature_importances_
for name, imp in sorted(zip(feature_names, importances), 
                        key=lambda x: x[1], reverse=True):
    print(f"{name}: {imp:.3f}")
```

**Checklist:**
- [ ] Model accuracy > 85%
- [ ] Precision > 80%
- [ ] Recall > 75%
- [ ] Cross-validation scores consistent
- [ ] No data leakage
- [ ] Feature importance makes sense
- [ ] Model saved to pkl file

---

### **Phase 4: Backend Integration (2 hours)**

#### **4.1: Risk Scorer Module**

**File: `ml/risk_scorer.py`**

**Interface:**
```python
import joblib
import numpy as np

class RiskScorer:
    def __init__(self, model_path='models/risk_model.pkl',
                 scaler_path='models/scaler.pkl'):
        self.model = joblib.load(model_path)
        self.scaler = joblib.load(scaler_path)
        self.feature_processor = FeatureProcessor()
    
    def score(self, transaction):
        '''
        Calculate risk score for a transaction
        
        Args:
            transaction: dict with transaction data
        
        Returns:
            dict with risk_score (0-100) and risk_level
        '''
        # Extract features
        features = self.feature_processor.engineer_features(transaction)
        X = np.array([list(features.values())])
        
        # Scale
        X_scaled = self.scaler.transform(X)
        
        # Predict probability
        prob = self.model.predict_proba(X_scaled)[0][1]
        
        # Convert to 0-100 scale
        risk_score = int(prob * 100)
        
        # Determine level
        if risk_score < 30:
            risk_level = "LOW"
        elif risk_score < 70:
            risk_level = "MEDIUM"
        else:
            risk_level = "HIGH"
        
        return {
            'risk_score': risk_score,
            'risk_level': risk_level,
            'fraud_probability': prob
        }
```

#### **4.2: Java-Python Integration**

**Python Flask API** (Alternative to direct integration)

**File: `ml/app.py`**

```python
from flask import Flask, request, jsonify
from risk_scorer import RiskScorer

app = Flask(__name__)
scorer = RiskScorer()

@app.route('/api/risk-score', methods=['POST'])
def calculate_risk():
    '''
    POST /api/risk-score
    {
        "transactionId": "TXN123...",
        "merchantUpi": "amazon@upi",
        "amount": 5000,
        "customerPhone": "+919876543210",
        "timestamp": "2025-02-27T14:30:00Z"
    }
    
    Returns:
    {
        "riskScore": 42,
        "riskLevel": "MEDIUM",
        "fraudProbability": 0.42
    }
    '''
    try:
        data = request.json
        result = scorer.score(data)
        return jsonify(result), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 400

if __name__ == '__main__':
    app.run(port=5000)
```

**Or: Java Spring Boot Service** (Direct integration)

Person A should call: `RiskScorerService.calculateRisk(transaction)`

```java
// In Person A's Java code
@Service
public class RiskScorerService {
    
    private RiskModel model;
    private Scaler scaler;
    
    public RiskScore calculateRisk(Transaction tx) {
        // Extract features
        double[] features = extractFeatures(tx);
        
        // Scale
        double[] scaled = scaler.transform(features);
        
        // Predict
        double fraudProb = model.predictProba(scaled);
        
        // Convert to 0-100
        int riskScore = (int)(fraudProb * 100);
        
        return new RiskScore(riskScore, getRiskLevel(riskScore));
    }
}
```

#### **4.3: Integration Testing**

**Tests:**
- [ ] Risk scorer loads successfully
- [ ] Score calculation works
- [ ] Scores reasonable for test cases
- [ ] Low fraud → LOW risk
- [ ] High fraud → HIGH risk
- [ ] API endpoint responds correctly
- [ ] Error handling works
- [ ] Latency < 100ms
- [ ] Handles edge cases (missing fields)

**Test Cases:**
```python
# Low risk transaction
low_risk = {
    'amount': 100,
    'merchant_upi': 'trusted@bank',
    'time': 14,  # Daytime
    'customer_history': 50  # Many txns
}
assert scorer.score(low_risk)['risk_level'] == 'LOW'

# High risk transaction
high_risk = {
    'amount': 99000,
    'merchant_upi': 'unknown@bank',
    'time': 2,  # 2 AM
    'customer_history': 0  # First time
}
assert scorer.score(high_risk)['risk_level'] == 'HIGH'
```

---

### **Phase 5: Optional Features (2 hours)**

#### **5.1: Merchant Dashboard** (if time permits)

**File: `frontend/src/components/MerchantDashboard.jsx`**

**Features:**
- [ ] Login page for merchants
- [ ] Dashboard showing their transactions
- [ ] Chargeback/dispute statistics
- [ ] Avg chargeback rate
- [ ] Most common dispute reasons
- [ ] Over-time trend chart
- [ ] Risk mitigation tips

```javascript
// Component structure
<MerchantDashboard>
  ├─ MerchantLogin (optional)
  ├─ TransactionList
  ├─ StatisticsCard
  │  ├─ Total Transactions
  │  ├─ Chargeback Rate
  │  ├─ Total Loss (₹)
  │  └─ Average RTO Time
  ├─ Charts
  │  ├─ Chargeback Trend
  │  └─ RTO Reasons
  └─ Recommendations
     └─ Best practices
```

#### **5.2: Risk Analytics Dashboard**

**Features:**
- [ ] System-wide statistics
- [ ] Total risk score distribution
- [ ] Fraud rate trends
- [ ] High-risk merchant list
- [ ] Geographic risk heatmap
- [ ] Time-based risk patterns
- [ ] Recommendations for improvement

**Backend Endpoint:**
```
GET /api/analytics/risk-summary
{
  "totalDisputes": 1024,
  "fraudRate": 0.12,  // 12%
  "avgRiskScore": 35,
  "topRiskMerchants": [...],
  "riskByCategory": {...},
  "riskByHour": {...}
}
```

---

### **Phase 6: Testing & Optimization (1 hour)**

#### **6.1: Model Testing**

**Tests:**
- [ ] All test metrics pass (>85% accuracy)
- [ ] Edge cases handled
- [ ] Missing features handled
- [ ] Outlier amounts handled
- [ ] Model robust to data variations

#### **6.2: Performance Optimization**

**Tasks:**
- [ ] Profile code (which functions slow?)
- [ ] Optimize slow functions
- [ ] Cache predictions (if same txn comes twice)
- [ ] Reduce memory footprint
- [ ] Optimize model size (quantization, pruning)
- [ ] Target latency: <100ms per prediction

```python
import time

start = time.time()
for _ in range(1000):
    result = scorer.score(test_transaction)
elapsed = time.time() - start
print(f"Avg latency: {elapsed/1000*1000:.2f}ms")  # Should be <100ms
```

#### **6.3: Monitoring**

**Logging:**
- [ ] Log each prediction
- [ ] Track prediction time
- [ ] Track error rates
- [ ] Alert if model accuracy drops

```python
import logging

logger = logging.getLogger(__name__)

def score_with_logging(transaction):
    start = time.time()
    result = scorer.score(transaction)
    elapsed = time.time() - start
    
    logger.info(f"RiskScore: {result['risk_score']}, " +
                f"Time: {elapsed*1000:.2f}ms")
    return result
```

---

### **Phase 7: Deployment & Documentation (1 hour)**

#### **7.1: Model Deployment**

**Tasks:**
- [ ] Export model to pickle/ONNX
- [ ] Create inference script
- [ ] Test model loading
- [ ] Document model usage
- [ ] Set up monitoring

#### **7.2: Documentation**

**File: `ml/README.md`**

```markdown
# Risk Scoring Model

## Overview
Machine learning model for predicting fraud/chargeback risk.

## Model Details
- Algorithm: Random Forest Classifier
- Accuracy: 87%
- Features: 15
- Training data: 10,000 transactions

## Usage
```python
from risk_scorer import RiskScorer

scorer = RiskScorer()
result = scorer.score({
    'amount': 5000,
    'merchant_upi': 'amazon@upi',
    ...
})
print(result['risk_score'])
```

## Performance
- Latency: <100ms
- Throughput: >100 predictions/sec
- Memory: <200MB

## Monitoring
- Check prediction accuracy monthly
- Retrain if accuracy drops below 85%
- Update features based on new fraud patterns
```

#### **7.3: CI/CD Integration**

**GitHub Actions Workflow** (optional)

```yaml
name: Test ML Model

on: push

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up Python
        uses: actions/setup-python@v2
        with:
          python-version: 3.9
      - name: Install dependencies
        run: pip install -r ml/requirements.txt
      - name: Run tests
        run: python -m pytest ml/tests/
      - name: Check model accuracy
        run: python ml/evaluate_model.py
```

---

## 🎯 Success Criteria

**By End of 12 Hours:**

- ✅ Python environment set up with all dependencies
- ✅ Risk scoring algorithm designed (8 factors)
- ✅ Features extracted from transaction data
- ✅ ML model trained with >85% accuracy
- ✅ Model evaluation complete
- ✅ Integration with backend working
- ✅ API endpoint returning risk scores
- ✅ Optional features partially implemented
- ✅ Code tested and optimized
- ✅ Documentation complete
- ✅ Model deployed and ready for use
- ✅ Monitoring/logging in place

---

## 📞 Communication with Team

| Time | Status | Notes |
|------|--------|-------|
| Hour 1 | ✓ Setup | Python env, dependencies, folder structure ready |
| Hour 2 | ✓ Algorithm | Risk scoring design with 8 factors complete |
| Hour 3 | ✓ Features | Feature engineering pipeline working |
| Hour 5 | ✓ Training | Model trained, >85% accuracy achieved |
| Hour 6 | ✓ Evaluation | All metrics pass, cross-validation good |
| Hour 8 | ✓ Integration | Backend integration complete, API working |
| Hour 10 | ✓ Optional | Dashboard started (if time permits) |
| Hour 11 | ✓ Testing | All tests pass, latency <100ms |
| Hour 12 | ✅ DONE | ML pipeline ready for production |

---

## 🛠️ Technologies Used

```
Language:     Python 3.9+
ML Libraries: scikit-learn, xgboost, tensorflow
Data:         pandas, numpy
Deployment:   joblib, pickle
Monitoring:   logging
Testing:      pytest
Visualization: matplotlib, seaborn
API:          Flask (if standalone service)
```

---

## 📚 Reference Documents

- [01_PROBLEM_STATEMENT.md](../01_PROBLEM_STATEMENT.md) - Market opportunity
- [02_SYSTEM_ARCHITECTURE.md](../02_SYSTEM_ARCHITECTURE.md) - System design
- [03_PRODUCT_SPECIFICATION.md](../03_PRODUCT_SPECIFICATION.md) - Features
- [04_API_CONTRACTS.md](../04_API_CONTRACTS.md) - API specs
- [05_TECH_STACK_AND_SETUP.md](../05_TECH_STACK_AND_SETUP.md) - Tech details
- [06_IMPLEMENTATION_GUIDE.md](../06_IMPLEMENTATION_GUIDE.md) - Code examples

---

## 💡 Tips for Success

1. **Start with baseline model** (Logistic Regression) first, then try Random Forest
2. **Feature engineering is key** - spend time on good features
3. **Cross-validate** - don't just use train/test split
4. **Monitor for data drift** - retrain monthly with new data
5. **Document features** - future you will appreciate it
6. **Test edge cases** - unusual amounts, new merchants, etc.
7. **Profile performance** - ensure latency < 100ms
8. **Version your model** - save old models for rollback

**Let's build intelligent risk detection! 🤖**
