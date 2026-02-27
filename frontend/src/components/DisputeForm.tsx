import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { disputeApi } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';
import { Loader2, AlertCircle, CheckCircle2 } from 'lucide-react';

interface FormData {
  transactionId: string;
  merchantUpi: string;
  amount: string;
  customerPhone: string;
}

interface FormErrors {
  transactionId?: string;
  merchantUpi?: string;
  amount?: string;
  customerPhone?: string;
}

interface DisputeFormProps {
  onSuccess?: (disputeId: string) => void;
}

export function DisputeForm({ onSuccess }: DisputeFormProps) {
  const { phone, isAuthenticated } = useAuth();
  const [formData, setFormData] = useState<FormData>({
    transactionId: '',
    merchantUpi: '',
    amount: '',
    customerPhone: phone || '+91',
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // Auto-fill phone if user is logged in
  useEffect(() => {
    if (isAuthenticated && phone) {
      setFormData(prev => ({ ...prev, customerPhone: phone }));
    }
  }, [phone, isAuthenticated]);

  const validateField = (name: keyof FormData, value: string): string | undefined => {
    switch (name) {
      case 'transactionId':
        if (!value) return 'Transaction ID is required';
        if (!/^TXN\d{14,}$/.test(value)) {
          return 'Transaction ID must start with TXN followed by at least 14 digits';
        }
        return undefined;

      case 'merchantUpi':
        if (!value) return 'Merchant UPI is required';
        if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$/.test(value)) {
          return 'Invalid UPI format (e.g., merchant@upi or merchant@bank)';
        }
        return undefined;

      case 'amount':
        if (!value) return 'Amount is required';
        const numAmount = parseFloat(value);
        if (isNaN(numAmount) || numAmount < 1 || numAmount > 100000) {
          return 'Amount must be between ₹1 and ₹100,000';
        }
        return undefined;

      case 'customerPhone':
        if (!value) return 'Phone number is required';
        if (!/^\+91\d{10}$/.test(value)) {
          return 'Phone must start with +91 followed by 10 digits';
        }
        return undefined;

      default:
        return undefined;
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    
    // Clear error when user starts typing
    if (errors[name as keyof FormErrors]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
    setSubmitError(null);
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const error = validateField(name as keyof FormData, value);
    if (error) {
      setErrors((prev) => ({ ...prev, [name]: error }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};
    let isValid = true;

    (Object.keys(formData) as Array<keyof FormData>).forEach((key) => {
      const error = validateField(key, formData[key]);
      if (error) {
        newErrors[key] = error;
        isValid = false;
      }
    });

    setErrors(newErrors);
    return isValid;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError(null);

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const response = await disputeApi.createDispute({
        transactionId: formData.transactionId,
        merchantUpi: formData.merchantUpi,
        amount: parseFloat(formData.amount),
        customerPhone: formData.customerPhone,
      });

      setSuccess(true);
      
      // Call onSuccess callback with dispute ID
      if (onSuccess && response.data.disputeId) {
        onSuccess(response.data.disputeId);
      }

      // Reset form
      setTimeout(() => {
        setFormData({
          transactionId: '',
          merchantUpi: '',
          amount: '',
          customerPhone: '+91',
        });
        setSuccess(false);
      }, 2000);

    } catch (error: any) {
      console.error('Failed to submit dispute:', error);
      setSubmitError(
        error.response?.data?.message || 
        'Failed to submit dispute. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  const isFormValid = () => {
    return (
      formData.transactionId &&
      formData.merchantUpi &&
      formData.amount &&
      formData.customerPhone &&
      Object.keys(errors).length === 0
    );
  };

  return (
    <div className="w-full max-w-2xl mx-auto p-6 bg-card rounded-lg border shadow-lg">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-foreground mb-2">
          File UPI Dispute
        </h2>
        <p className="text-muted-foreground">
          Resolve your failed UPI transaction in 2-4 hours
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Transaction ID */}
        <div className="space-y-2">
          <Label htmlFor="transactionId">
            Transaction ID <span className="text-destructive">*</span>
          </Label>
          <Input
            id="transactionId"
            name="transactionId"
            value={formData.transactionId}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="TXN20260227123456"
            className={errors.transactionId ? 'border-destructive' : ''}
          />
          <p className="text-xs text-muted-foreground">
            Find in your transaction history
          </p>
          {errors.transactionId && (
            <p className="text-sm text-destructive flex items-center gap-1">
              <AlertCircle className="w-4 h-4" />
              {errors.transactionId}
            </p>
          )}
        </div>

        {/* Merchant UPI */}
        <div className="space-y-2">
          <Label htmlFor="merchantUpi">
            Merchant UPI <span className="text-destructive">*</span>
          </Label>
          <Input
            id="merchantUpi"
            name="merchantUpi"
            value={formData.merchantUpi}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="amazon@upi"
            className={errors.merchantUpi ? 'border-destructive' : ''}
          />
          <p className="text-xs text-muted-foreground">
            Ask merchant or check receipt
          </p>
          {errors.merchantUpi && (
            <p className="text-sm text-destructive flex items-center gap-1">
              <AlertCircle className="w-4 h-4" />
              {errors.merchantUpi}
            </p>
          )}
        </div>

        {/* Amount */}
        <div className="space-y-2">
          <Label htmlFor="amount">
            Amount (₹) <span className="text-destructive">*</span>
          </Label>
          <Input
            id="amount"
            name="amount"
            type="number"
            value={formData.amount}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="5000"
            min="1"
            max="100000"
            className={errors.amount ? 'border-destructive' : ''}
          />
          <p className="text-xs text-muted-foreground">
            Maximum ₹100,000 per dispute
          </p>
          {errors.amount && (
            <p className="text-sm text-destructive flex items-center gap-1">
              <AlertCircle className="w-4 h-4" />
              {errors.amount}
            </p>
          )}
        </div>

        {/* Customer Phone */}
        <div className="space-y-2">
          <Label htmlFor="customerPhone">
            Your Phone Number <span className="text-destructive">*</span>
          </Label>
          <Input
            id="customerPhone"
            name="customerPhone"
            value={formData.customerPhone}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="+919876543210"
            className={errors.customerPhone ? 'border-destructive' : ''}
          />
          <p className="text-xs text-muted-foreground">
            We'll send SMS updates here
          </p>
          {errors.customerPhone && (
            <p className="text-sm text-destructive flex items-center gap-1">
              <AlertCircle className="w-4 h-4" />
              {errors.customerPhone}
            </p>
          )}
        </div>

        {/* Submit Error */}
        {submitError && (
          <div className="p-4 bg-destructive/10 border border-destructive rounded-md">
            <p className="text-sm text-destructive flex items-center gap-2">
              <AlertCircle className="w-4 h-4" />
              {submitError}
            </p>
          </div>
        )}

        {/* Success Message */}
        {success && (
          <div className="p-4 bg-green-50 dark:bg-green-950/20 border border-green-500 rounded-md">
            <p className="text-sm text-green-700 dark:text-green-400 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              Dispute submitted successfully!
            </p>
          </div>
        )}

        {/* Submit Button */}
        <Button
          type="submit"
          size="lg"
          className="w-full"
          disabled={loading || !isFormValid() || success}
        >
          {loading ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              Processing...
            </>
          ) : success ? (
            <>
              <CheckCircle2 className="w-4 h-4" />
              Submitted!
            </>
          ) : (
            'Submit Dispute'
          )}
        </Button>
      </form>
    </div>
  );
}
