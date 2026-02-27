import { useEffect, useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { LogOut, FileText, Clock, CheckCircle, XCircle } from 'lucide-react';
import { disputeApi } from '@/lib/api';

interface Dispute {
  disputeId: string;
  transactionId: string;
  amount: number;
  status: string;
  createdAt: string;
}

export function UserDashboard() {
  const { phone, logout } = useAuth();
  const [disputes, setDisputes] = useState<Dispute[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchUserDisputes();
  }, [phone]);

  const fetchUserDisputes = async () => {
    try {
      if (!phone) return;
      
      // Call backend API
      const response = await disputeApi.getUserDisputes(phone);
      setDisputes(response.data.disputes || []);
    } catch (error) {
      console.error('Failed to fetch disputes:', error);
      // Mock data as fallback
      setDisputes([]);
    } finally {
      setIsLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return <CheckCircle className="w-5 h-5 text-green-500" />;
      case 'REJECTED':
        return <XCircle className="w-5 h-5 text-red-500" />;
      case 'PENDING':
        return <Clock className="w-5 h-5 text-yellow-500" />;
      default:
        return <FileText className="w-5 h-5 text-gray-500" />;
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold mb-2">My Disputes</h1>
          <p className="text-muted-foreground">Logged in as {phone}</p>
        </div>
        <Button variant="outline" onClick={logout}>
          <LogOut className="w-4 h-4 mr-2" />
          Logout
        </Button>
      </div>

      {/* Disputes List */}
      {isLoading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading your disputes...</p>
        </div>
      ) : disputes.length === 0 ? (
        <div className="text-center py-12 border rounded-lg bg-card">
          <FileText className="w-16 h-16 text-muted-foreground mx-auto mb-4" />
          <h3 className="text-xl font-semibold mb-2">No disputes yet</h3>
          <p className="text-muted-foreground">File your first dispute to see it here</p>
        </div>
      ) : (
        <div className="space-y-4">
          {disputes.map((dispute) => (
            <div 
              key={dispute.disputeId}
              className="p-6 border rounded-lg bg-card hover:shadow-md transition-shadow"
            >
              <div className="flex justify-between items-start mb-4">
                <div>
                  <div className="flex items-center gap-2 mb-2">
                    <h3 className="text-lg font-semibold">{dispute.disputeId}</h3>
                    {getStatusIcon(dispute.status)}
                  </div>
                  <p className="text-sm text-muted-foreground">
                    Transaction: {dispute.transactionId}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-2xl font-bold text-primary">₹{dispute.amount.toLocaleString()}</p>
                  <p className="text-xs text-muted-foreground mt-1">
                    {new Date(dispute.createdAt).toLocaleDateString('en-IN')}
                  </p>
                </div>
              </div>
              
              <div className="flex items-center justify-between pt-4 border-t">
                <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                  dispute.status === 'APPROVED' ? 'bg-green-500/10 text-green-500' :
                  dispute.status === 'REJECTED' ? 'bg-red-500/10 text-red-500' :
                  'bg-yellow-500/10 text-yellow-500'
                }`}>
                  {dispute.status}
                </span>
                <Button variant="ghost" size="sm">View Details</Button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
