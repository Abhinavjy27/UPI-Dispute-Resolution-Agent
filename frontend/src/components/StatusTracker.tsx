import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { disputeApi } from '@/lib/api';
import {
  Loader2,
  AlertCircle,
  CheckCircle2,
  Clock,
  XCircle,
  Copy,
  Search,
} from 'lucide-react';

interface DisputeStatus {
  disputeId: string;
  transactionId: string;
  merchantUpi: string;
  amount: number;
  customerPhone: string;
  status: 'REFUND_INITIATED' | 'REJECTED' | 'MANUAL_REVIEW' | 'PENDING';
  message: string;
  neftReferenceNumber?: string;
  createdAt: string;
  updatedAt?: string;
}

interface StatusTrackerProps {
  disputeId?: string;
}

export function StatusTracker({ disputeId: initialDisputeId }: StatusTrackerProps) {
  const [disputeId, setDisputeId] = useState(initialDisputeId || '');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [disputeStatus, setDisputeStatus] = useState<DisputeStatus | null>(null);
  const [copied, setCopied] = useState<string | null>(null);

  useEffect(() => {
    if (initialDisputeId) {
      fetchDisputeStatus(initialDisputeId);
    }
  }, [initialDisputeId]);

  const fetchDisputeStatus = async (id: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await disputeApi.getDisputeById(id);
      setDisputeStatus(response.data);
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
        'Dispute not found. Please check the ID and try again.'
      );
      setDisputeStatus(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (disputeId.trim()) {
      fetchDisputeStatus(disputeId.trim());
    }
  };

  const copyToClipboard = (text: string, type: string) => {
    navigator.clipboard.writeText(text);
    setCopied(type);
    setTimeout(() => setCopied(null), 2000);
  };

  const getStatusBadge = (status: DisputeStatus['status']) => {
    switch (status) {
      case 'REFUND_INITIATED':
        return (
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-green-100 dark:bg-green-950/30 border border-green-500">
            <CheckCircle2 className="w-5 h-5 text-green-600 dark:text-green-400" />
            <span className="text-sm font-semibold text-green-700 dark:text-green-400">
              Refund Initiated
            </span>
          </div>
        );
      case 'REJECTED':
        return (
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-red-100 dark:bg-red-950/30 border border-red-500">
            <XCircle className="w-5 h-5 text-red-600 dark:text-red-400" />
            <span className="text-sm font-semibold text-red-700 dark:text-red-400">
              Rejected
            </span>
          </div>
        );
      case 'MANUAL_REVIEW':
        return (
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-yellow-100 dark:bg-yellow-950/30 border border-yellow-500">
            <Clock className="w-5 h-5 text-yellow-600 dark:text-yellow-400" />
            <span className="text-sm font-semibold text-yellow-700 dark:text-yellow-400">
              Manual Review
            </span>
          </div>
        );
      default:
        return (
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-blue-100 dark:bg-blue-950/30 border border-blue-500">
            <Loader2 className="w-5 h-5 text-blue-600 dark:text-blue-400 animate-spin" />
            <span className="text-sm font-semibold text-blue-700 dark:text-blue-400">
              Processing
            </span>
          </div>
        );
    }
  };

  return (
    <div className="w-full max-w-2xl mx-auto p-6">
      {/* Search Form */}
      {!initialDisputeId && (
        <div className="mb-8 p-6 bg-card rounded-lg border shadow-lg">
          <h2 className="text-2xl font-bold text-foreground mb-4">
            Track Your Dispute
          </h2>
          <form onSubmit={handleSearch} className="flex gap-2">
            <div className="flex-1">
              <Input
                id="searchDisputeId"
                value={disputeId}
                onChange={(e) => setDisputeId(e.target.value)}
                placeholder="Enter Dispute ID (e.g., DIS_1709028600)"
                className="w-full"
              />
            </div>
            <Button type="submit" disabled={loading || !disputeId.trim()}>
              {loading ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <>
                  <Search className="w-4 h-4" />
                  Search
                </>
              )}
            </Button>
          </form>
        </div>
      )}

      {/* Error Message */}
      {error && (
        <div className="mb-6 p-4 bg-destructive/10 border border-destructive rounded-md">
          <p className="text-sm text-destructive flex items-center gap-2">
            <AlertCircle className="w-4 h-4" />
            {error}
          </p>
        </div>
      )}

      {/* Dispute Status Display */}
      {disputeStatus && (
        <div className="space-y-6">
          {/* Status Badge */}
          <div className="flex flex-col items-center justify-center p-6 bg-card rounded-lg border shadow-lg">
            {getStatusBadge(disputeStatus.status)}
            <p className="mt-4 text-lg text-center text-foreground font-medium">
              {disputeStatus.message}
            </p>
          </div>

          {/* Dispute Details */}
          <div className="p-6 bg-card rounded-lg border shadow-lg">
            <h3 className="text-lg font-semibold text-foreground mb-4">
              Dispute Details
            </h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Dispute ID:</span>
                <div className="flex items-center gap-2">
                  <span className="text-sm font-mono font-semibold">
                    {disputeStatus.disputeId}
                  </span>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => copyToClipboard(disputeStatus.disputeId, 'disputeId')}
                  >
                    {copied === 'disputeId' ? (
                      <CheckCircle2 className="w-4 h-4 text-green-500" />
                    ) : (
                      <Copy className="w-4 h-4" />
                    )}
                  </Button>
                </div>
              </div>

              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Transaction ID:</span>
                <span className="text-sm font-mono">{disputeStatus.transactionId}</span>
              </div>

              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Amount:</span>
                <span className="text-sm font-semibold">
                  ₹{disputeStatus.amount.toLocaleString('en-IN')}
                </span>
              </div>

              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Merchant UPI:</span>
                <span className="text-sm font-mono">{disputeStatus.merchantUpi}</span>
              </div>

              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Created:</span>
                <span className="text-sm">
                  {new Date(disputeStatus.createdAt).toLocaleString('en-IN')}
                </span>
              </div>
            </div>
          </div>

          {/* NEFT Details (if refund initiated) */}
          {disputeStatus.status === 'REFUND_INITIATED' && disputeStatus.neftReferenceNumber && (
            <div className="p-6 bg-green-50 dark:bg-green-950/20 rounded-lg border border-green-500">
              <h3 className="text-lg font-semibold text-green-700 dark:text-green-400 mb-4">
                NEFT Refund Details
              </h3>
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-green-600 dark:text-green-500">
                    NEFT Reference Number:
                  </span>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-lg font-mono font-bold text-green-700 dark:text-green-400">
                      {disputeStatus.neftReferenceNumber}
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() =>
                        copyToClipboard(disputeStatus.neftReferenceNumber!, 'neft')
                      }
                    >
                      {copied === 'neft' ? (
                        <CheckCircle2 className="w-4 h-4 text-green-500" />
                      ) : (
                        <Copy className="w-4 h-4" />
                      )}
                    </Button>
                  </div>
                </div>
                <p className="text-sm text-green-600 dark:text-green-500">
                  Refund will be credited within 24-48 hours. You'll receive an SMS notification
                  when the amount is credited to your account.
                </p>
              </div>
            </div>
          )}

          {/* Timeline */}
          <div className="p-6 bg-card rounded-lg border shadow-lg">
            <h3 className="text-lg font-semibold text-foreground mb-4">Timeline</h3>
            <div className="space-y-4">
              <div className="flex items-start gap-3">
                <CheckCircle2 className="w-5 h-5 text-green-500 mt-0.5" />
                <div>
                  <p className="text-sm font-medium">Dispute Filed</p>
                  <p className="text-xs text-muted-foreground">
                    {new Date(disputeStatus.createdAt).toLocaleString('en-IN')}
                  </p>
                </div>
              </div>

              {disputeStatus.status === 'REFUND_INITIATED' && (
                <>
                  <div className="flex items-start gap-3">
                    <CheckCircle2 className="w-5 h-5 text-green-500 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium">Transaction Verified</p>
                      <p className="text-xs text-muted-foreground">
                        Verified with bank authorities
                      </p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <CheckCircle2 className="w-5 h-5 text-green-500 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium">Refund Initiated</p>
                      <p className="text-xs text-muted-foreground">
                        NEFT transfer in progress
                      </p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <Clock className="w-5 h-5 text-yellow-500 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium">Money Arriving</p>
                      <p className="text-xs text-muted-foreground">
                        Expected within 24-48 hours
                      </p>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
