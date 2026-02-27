import React, { useState } from 'react'
import './App.css'

function App() {
  const [formData, setFormData] = useState({
    transactionId: '',
    merchantUPI: '',
    amount: '',
    phone: '',
    reason: ''
  })
  
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [statusChecking, setStatusChecking] = useState(false)
  const [statusId, setStatusId] = useState('')

  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResponse(null)

    try {
      const res = await fetch(`${API_URL}/api/disputes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      })

      if (!res.ok) {
        throw new Error(`Error: ${res.statusText}`)
      }

      const data = await res.json()
      setResponse(data)
      setFormData({
        transactionId: '',
        merchantUPI: '',
        amount: '',
        phone: '',
        reason: ''
      })
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const checkStatus = async (e) => {
    e.preventDefault()
    if (!statusId) {
      setError('Please enter a dispute ID')
      return
    }

    setStatusChecking(true)
    setError(null)

    try {
      const res = await fetch(`${API_URL}/api/disputes/${statusId}`)
      
      if (!res.ok) {
        throw new Error(`Dispute not found`)
      }

      const data = await res.json()
      setResponse(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setStatusChecking(false)
    }
  }

  return (
    <div className="container">
      <h1>UPI Dispute Resolution</h1>

      <div className="forms-wrapper">
        {/* File Dispute Form */}
        <div className="form-section">
          <h2>File a Dispute</h2>
          <form onSubmit={handleSubmit}>
            <input
              type="text"
              name="transactionId"
              placeholder="Transaction ID"
              value={formData.transactionId}
              onChange={handleChange}
              required
            />
            <input
              type="text"
              name="merchantUPI"
              placeholder="Merchant UPI"
              value={formData.merchantUPI}
              onChange={handleChange}
              required
            />
            <input
              type="number"
              name="amount"
              placeholder="Amount"
              value={formData.amount}
              onChange={handleChange}
              required
            />
            <input
              type="tel"
              name="phone"
              placeholder="Phone (10 digits)"
              value={formData.phone}
              onChange={handleChange}
              pattern="[0-9]{10}"
              required
            />
            <textarea
              name="reason"
              placeholder="Reason for dispute"
              value={formData.reason}
              onChange={handleChange}
              rows="3"
            ></textarea>
            <button type="submit" disabled={loading}>
              {loading ? 'Filing...' : 'File Dispute'}
            </button>
          </form>
        </div>

        {/* Check Status Form */}
        <div className="form-section">
          <h2>Check Dispute Status</h2>
          <form onSubmit={checkStatus}>
            <input
              type="text"
              placeholder="Dispute ID"
              value={statusId}
              onChange={(e) => setStatusId(e.target.value)}
              required
            />
            <button type="submit" disabled={statusChecking}>
              {statusChecking ? 'Checking...' : 'Check Status'}
            </button>
          </form>
        </div>
      </div>

      {error && <div className="error">{error}</div>}

      {response && (
        <div className="response">
          <h3>Response</h3>
          <pre>{JSON.stringify(response, null, 2)}</pre>
        </div>
      )}
    </div>
  )
}

export default App
