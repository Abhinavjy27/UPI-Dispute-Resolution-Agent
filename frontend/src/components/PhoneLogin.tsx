import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/contexts/AuthContext';
import { Phone, AlertCircle } from 'lucide-react';

interface PhoneLoginProps {
  onSuccess?: () => void;
}

export function PhoneLogin({ onSuccess }: PhoneLoginProps) {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();

  const validatePhone = (phone: string): boolean => {
    const phoneRegex = /^[6-9]\d{9}$/;
    return phoneRegex.test(phone);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validatePhone(phoneNumber)) {
      setError('Please enter a valid 10-digit Indian mobile number');
      return;
    }

    setIsLoading(true);

    try {
      // Format with +91
      const fullPhone = `+91${phoneNumber}`;
      
      // In a real app, you'd call your backend here:
      // const response = await api.loginOrSignup({ phone: fullPhone });
      // For now, we just store locally
      
      login(fullPhone);
      onSuccess?.();
    } catch (err) {
      setError('Something went wrong. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto p-8 bg-card rounded-lg border shadow-lg">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-purple-500/10 rounded-full flex items-center justify-center mx-auto mb-4">
          <Phone className="w-8 h-8 text-purple-500" />
        </div>
        <h2 className="text-2xl font-bold mb-2">Welcome Back</h2>
        <p className="text-muted-foreground text-sm">
          Enter your phone number to continue
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="phone">Phone Number</Label>
          <div className="flex gap-2">
            <div className="flex items-center justify-center px-3 bg-muted rounded-md border">
              <span className="text-sm font-medium">+91</span>
            </div>
            <Input
              id="phone"
              type="tel"
              placeholder="9876543210"
              value={phoneNumber}
              onChange={(e) => {
                const value = e.target.value.replace(/\D/g, '').slice(0, 10);
                setPhoneNumber(value);
                setError('');
              }}
              className="flex-1"
              disabled={isLoading}
            />
          </div>
          {error && (
            <div className="flex items-center gap-2 text-sm text-destructive">
              <AlertCircle className="w-4 h-4" />
              {error}
            </div>
          )}
        </div>

        <Button 
          type="submit" 
          className="w-full"
          disabled={isLoading || phoneNumber.length !== 10}
        >
          {isLoading ? 'Please wait...' : 'Continue'}
        </Button>
      </form>

      <p className="text-xs text-muted-foreground text-center mt-6">
        By continuing, you agree to our Terms of Service
      </p>
    </div>
  );
}
