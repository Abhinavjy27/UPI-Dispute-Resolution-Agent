import { useState } from 'react'
import { HeroSection } from '@/components/ui/hero-section'
import { DisputeForm } from '@/components/DisputeForm'
import { StatusTracker } from '@/components/StatusTracker'
import { Button } from '@/components/ui/button'
import { PhoneLogin } from '@/components/PhoneLogin'
import { UserDashboard } from '@/components/UserDashboard'
import { RetroGrid } from '@/components/ui/retro-grid'
import { useAuth } from '@/contexts/AuthContext'
import { Search, LogOut, User, Shield, Zap, Banknote, ArrowRight, CheckCircle2, Brain } from 'lucide-react'
import './App.css'

function App() {
  const [view, setView] = useState<'home' | 'form' | 'status' | 'login' | 'dashboard'>('home')
  const [currentDisputeId, setCurrentDisputeId] = useState<string | undefined>(undefined)
  const { isAuthenticated, logout } = useAuth()

  const handleDisputeSuccess = (disputeId: string) => {
    setCurrentDisputeId(disputeId)
    setView('status')
  }

  const scrollToForm = () => {
    if (!isAuthenticated) {
      setView('login');
      return;
    }
    setView('form')
    setTimeout(() => {
      document.getElementById('dispute-form')?.scrollIntoView({ behavior: 'smooth' })
    }, 100)
  }

  const handleTrackDispute = () => {
    if (!isAuthenticated) {
      setView('login');
      return;
    }
    setCurrentDisputeId(undefined);
    setView('status');
  }

  return (
    <div className="min-h-screen bg-background relative">
      {/* Fixed Animated Grid Background */}
      <RetroGrid 
        angle={65}
        cellSize={50}
        opacity={0.35}
        lightLineColor="#3a3a3a"
        darkLineColor="#1a1a1a"
      />
      
      {/* All content with relative positioning to stay above grid */}
      <div className="relative z-10">
        {/* Login Screen */}
        {view === 'login' && (
        <div className="min-h-screen flex flex-col">
          {/* Back to Home Button */}
          <div className="p-4">
            <Button
              onClick={() => setView('home')}
              variant="ghost"
              size="sm"
            >
              ← Back to Home
            </Button>
          </div>
          <div className="flex-1 flex items-center justify-center p-4">
            <PhoneLogin onSuccess={() => {
              // Redirect to form after login since that's the main action
              setView('form');
            }} />
          </div>
        </div>
      )}

      {/* Dashboard */}
      {view === 'dashboard' && isAuthenticated && (
        <div className="min-h-screen">
          {/* Dashboard Header */}
          <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="container flex h-16 items-center justify-between px-4 md:px-8">
              <button
                onClick={() => setView('home')}
                className="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-500 bg-clip-text text-transparent"
              >
                UPI Dispute Resolution
              </button>
              <nav className="flex gap-4 items-center">
                <button
                  onClick={() => setView('form')}
                  className="text-sm font-medium transition-colors hover:text-primary text-muted-foreground"
                >
                  File New Dispute
                </button>
                <Button
                  onClick={logout}
                  variant="ghost"
                  size="sm"
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  Logout
                </Button>
              </nav>
            </div>
          </header>
          <UserDashboard />
        </div>
      )}

      {/* Hero Section with Top Nav */}
      {view === 'home' && (
        <>
          {/* Top Navigation Bar */}
          <header className="absolute top-0 left-0 right-0 z-50 bg-transparent">
            <div className="container mx-auto px-4 md:px-8 py-4">
              <div className="flex justify-between items-center">
                <div className="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-500 bg-clip-text text-transparent">
                  UPI Dispute Resolution
                </div>
                <div className="flex gap-3 items-center">
                  {isAuthenticated ? (
                    <>
                      <Button
                        onClick={() => setView('dashboard')}
                        variant="ghost"
                        size="sm"
                        className="text-sm font-medium"
                      >
                        <User className="w-4 h-4 mr-2" />
                        Profile
                      </Button>
                      <Button
                        onClick={logout}
                        variant="outline"
                        size="sm"
                        className="text-sm font-medium"
                      >
                        <LogOut className="w-4 h-4 mr-2" />
                        Logout
                      </Button>
                    </>
                  ) : (
                    <>
                      <Button
                        onClick={() => setView('login')}
                        variant="ghost"
                        size="sm"
                        className="text-sm font-medium"
                      >
                        Login
                      </Button>
                      <Button
                        onClick={() => setView('login')}
                        variant="default"
                        size="sm"
                        className="text-sm font-medium bg-gradient-to-r from-purple-600 to-pink-500 hover:from-purple-700 hover:to-pink-600"
                      >
                        Sign Up
                      </Button>
                    </>
                  )}
                </div>
              </div>
            </div>
          </header>

          <HeroSection
            title="Fast & Secure UPI Dispute Resolution"
            subtitle={{
              regular: "Resolve failed UPI transactions in ",
              gradient: "2-4 hours, not 5-7 days.",
            }}
            description="Automated AI-powered system verifies your failed UPI payment with banks, detects fraud, and initiates NEFT refunds instantly. No manual paperwork, no waiting weeks."
            ctaText="File Dispute Now"
            onCtaClick={scrollToForm}
            gridOptions={{
              angle: 65,
              opacity: 0.4,
              cellSize: 50,
              lightLineColor: "#4a4a4a",
              darkLineColor: "#2a2a2a",
            }}
          />
        </>
      )}

      {/* Navigation Header (when not on home) */}
      {view !== 'home' && view !== 'login' && view !== 'dashboard' && (
        <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
          <div className="container flex h-16 items-center justify-between px-4 md:px-8">
            <button
              onClick={() => setView('home')}
              className="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-500 bg-clip-text text-transparent"
            >
              UPI Dispute Resolution
            </button>
            <nav className="flex gap-4 items-center">
              <button
                onClick={() => {
                  if (!isAuthenticated) {
                    setView('login');
                    return;
                  }
                  setView('form');
                }}
                className={`text-sm font-medium transition-colors hover:text-primary ${
                  view === 'form' ? 'text-primary' : 'text-muted-foreground'
                }`}
              >
                File Dispute
              </button>
              <button
                onClick={handleTrackDispute}
                className={`text-sm font-medium transition-colors hover:text-primary ${
                  view === 'status' ? 'text-primary' : 'text-muted-foreground'
                }`}
              >
                Track Status
              </button>
              {isAuthenticated ? (
                <>
                  <Button
                    onClick={() => setView('dashboard')}
                    variant="ghost"
                    size="sm"
                  >
                    <User className="w-4 h-4 mr-2" />
                    Dashboard
                  </Button>
                  <Button
                    onClick={logout}
                    variant="ghost"
                    size="sm"
                  >
                    <LogOut className="w-4 h-4" />
                  </Button>
                </>
              ) : (
                <Button
                  onClick={() => setView('login')}
                  variant="ghost"
                  size="sm"
                >
                  Login
                </Button>
              )}
            </nav>
          </div>
        </header>
      )}

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8 md:px-8">
        {/* Action Buttons on Home */}
        {view === 'home' && (
          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center -mt-20 relative z-10">
            <div className="flex gap-4">
              <Button
                onClick={handleTrackDispute}
                variant="outline"
                size="lg"
                className="h-11 px-8 text-sm font-medium border-2 border-purple-500/50 hover:border-purple-500 hover:bg-purple-500/10 transition-all shadow-lg"
              >
                <Search className="w-4 h-4 mr-2" />
                Track Existing Dispute
              </Button>
              {isAuthenticated && (
                <Button
                  onClick={() => setView('dashboard')}
                  variant="outline"
                  size="lg"
                  className="h-11 px-8 text-sm font-medium border-2 border-purple-500/50 hover:border-purple-500 hover:bg-purple-500/10 transition-all shadow-lg"
                >
                  <User className="w-4 h-4 mr-2" />
                  My Dashboard
                </Button>
              )}
            </div>
          </div>
        )}

        {/* Dispute Form */}
        {view === 'form' && (
          <div id="dispute-form" className="py-8">
            {!isAuthenticated ? (
              <div className="text-center py-12">
                <div className="max-w-md mx-auto p-8 border rounded-lg bg-card">
                  <User className="w-16 h-16 mx-auto mb-4 text-muted-foreground" />
                  <h2 className="text-2xl font-bold mb-2">Login Required</h2>
                  <p className="text-muted-foreground mb-6">
                    Please login to file a dispute
                  </p>
                  <Button onClick={() => setView('login')} className="w-full">
                    Login / Sign Up
                  </Button>
                </div>
              </div>
            ) : (
              <>
                <DisputeForm onSuccess={handleDisputeSuccess} />
                <div className="mt-6 text-center">
                  <button
                    onClick={() => {
                      setCurrentDisputeId(undefined)
                      setView('status')
                    }}
                    className="text-sm text-muted-foreground hover:text-primary transition-colors"
                  >
                    Already have a dispute ID? Track it here →
                  </button>
                </div>
              </>
            )}
          </div>
        )}

        {/* Status Tracker */}
        {view === 'status' && (
          <div className="py-8">
            {!isAuthenticated ? (
              <div className="text-center py-12">
                <div className="max-w-md mx-auto p-8 border rounded-lg bg-card">
                  <Search className="w-16 h-16 mx-auto mb-4 text-muted-foreground" />
                  <h2 className="text-2xl font-bold mb-2">Login Required</h2>
                  <p className="text-muted-foreground mb-6">
                    Please login to track your disputes
                  </p>
                  <Button onClick={() => setView('login')} className="w-full">
                    Login / Sign Up
                  </Button>
                </div>
              </div>
            ) : (
              <>
                <StatusTracker disputeId={currentDisputeId} />
                <div className="mt-6 text-center">
                  <button
                    onClick={() => setView('form')}
                    className="text-sm text-muted-foreground hover:text-primary transition-colors"
                  >
                    ← File a new dispute
                  </button>
                </div>
              </>
            )}
          </div>
        )}

        {/* Features Section on Home */}
        {view === 'home' && (
          <section className="mt-32 mb-16 px-4">
            <div className="text-center mb-16">
              <h2 className="text-4xl md:text-5xl font-bold mb-4 bg-gradient-to-r from-purple-600 via-pink-500 to-orange-400 bg-clip-text text-transparent">
                How It Works
              </h2>
              <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
                Resolve your failed UPI transactions in 3 simple steps with AI-powered automation
              </p>
            </div>
            
            <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto relative">
              {/* Connecting Lines (hidden on mobile) */}
              <div className="hidden md:block absolute top-24 left-1/4 right-1/4 h-0.5 bg-gradient-to-r from-purple-500/50 via-pink-500/50 to-orange-500/50" />
              
              {/* Step 1 */}
              <div className="group relative">
                <div className="absolute -inset-0.5 bg-gradient-to-r from-purple-600 to-pink-600 rounded-2xl opacity-0 group-hover:opacity-100 blur transition duration-500" />
                <div className="relative p-8 border border-purple-500/20 rounded-2xl bg-card hover:bg-card/50 transition-all duration-300 hover:shadow-2xl hover:shadow-purple-500/20">
                  {/* Icon */}
                  <div className="relative mb-6">
                    <div className="absolute inset-0 bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl opacity-20 blur-xl" />
                    <div className="relative w-16 h-16 bg-gradient-to-br from-purple-500 to-pink-500 rounded-2xl flex items-center justify-center transform group-hover:scale-110 transition-transform duration-300">
                      <Shield className="w-8 h-8 text-white" />
                    </div>
                    <div className="absolute -top-2 -right-2 w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                      1
                    </div>
                  </div>
                  
                  {/* Content */}
                  <h3 className="text-2xl font-bold mb-3 group-hover:text-purple-500 transition-colors">
                    Verify Transaction
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    Enter failed transaction details. We verify with bank APIs in <span className="text-purple-500 font-semibold">real-time</span>.
                  </p>
                  
                  {/* Features List */}
                  <div className="mt-4 space-y-2">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <CheckCircle2 className="w-4 h-4 text-purple-500" />
                      <span>Instant bank verification</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <CheckCircle2 className="w-4 h-4 text-purple-500" />
                      <span>Secure API integration</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Arrow 1 */}
              <div className="hidden md:flex absolute top-24 left-1/3 transform -translate-x-1/2 z-10">
                <div className="bg-gradient-to-r from-purple-500 to-pink-500 rounded-full p-2">
                  <ArrowRight className="w-5 h-5 text-white" />
                </div>
              </div>

              {/* Step 2 */}
              <div className="group relative">
                <div className="absolute -inset-0.5 bg-gradient-to-r from-pink-600 to-orange-600 rounded-2xl opacity-0 group-hover:opacity-100 blur transition duration-500" />
                <div className="relative p-8 border border-pink-500/20 rounded-2xl bg-card hover:bg-card/50 transition-all duration-300 hover:shadow-2xl hover:shadow-pink-500/20">
                  {/* Icon */}
                  <div className="relative mb-6">
                    <div className="absolute inset-0 bg-gradient-to-r from-pink-500 to-orange-500 rounded-2xl opacity-20 blur-xl" />
                    <div className="relative w-16 h-16 bg-gradient-to-br from-pink-500 to-orange-500 rounded-2xl flex items-center justify-center transform group-hover:scale-110 transition-transform duration-300">
                      <Brain className="w-8 h-8 text-white" />
                    </div>
                    <div className="absolute -top-2 -right-2 w-8 h-8 bg-pink-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                      2
                    </div>
                  </div>
                  
                  {/* Content */}
                  <h3 className="text-2xl font-bold mb-3 group-hover:text-pink-500 transition-colors">
                    AI Fraud Check
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    ML model analyzes risk score. <span className="text-pink-500 font-semibold">Auto-approve</span> if LOW risk, instant rejection if HIGH.
                  </p>
                  
                  {/* Features List */}
                  <div className="mt-4 space-y-2">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Zap className="w-4 h-4 text-pink-500" />
                      <span>Lightning-fast decision</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Brain className="w-4 h-4 text-pink-500" />
                      <span>99% accuracy rate</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Arrow 2 */}
              <div className="hidden md:flex absolute top-24 left-2/3 transform -translate-x-1/2 z-10">
                <div className="bg-gradient-to-r from-pink-500 to-orange-500 rounded-full p-2">
                  <ArrowRight className="w-5 h-5 text-white" />
                </div>
              </div>

              {/* Step 3 */}
              <div className="group relative">
                <div className="absolute -inset-0.5 bg-gradient-to-r from-orange-600 to-yellow-600 rounded-2xl opacity-0 group-hover:opacity-100 blur transition duration-500" />
                <div className="relative p-8 border border-orange-500/20 rounded-2xl bg-card hover:bg-card/50 transition-all duration-300 hover:shadow-2xl hover:shadow-orange-500/20">
                  {/* Icon */}
                  <div className="relative mb-6">
                    <div className="absolute inset-0 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-2xl opacity-20 blur-xl" />
                    <div className="relative w-16 h-16 bg-gradient-to-br from-orange-500 to-yellow-500 rounded-2xl flex items-center justify-center transform group-hover:scale-110 transition-transform duration-300">
                      <Banknote className="w-8 h-8 text-white" />
                    </div>
                    <div className="absolute -top-2 -right-2 w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                      3
                    </div>
                  </div>
                  
                  {/* Content */}
                  <h3 className="text-2xl font-bold mb-3 group-hover:text-orange-500 transition-colors">
                    NEFT Refund
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    Automatic NEFT initiation. Money back in <span className="text-orange-500 font-semibold">24-48 hours</span> with SMS confirmation.
                  </p>
                  
                  {/* Features List */}
                  <div className="mt-4 space-y-2">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <CheckCircle2 className="w-4 h-4 text-orange-500" />
                      <span>Direct bank transfer</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <CheckCircle2 className="w-4 h-4 text-orange-500" />
                      <span>SMS + Email alerts</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Stats Section */}
            <div className="mt-20 grid grid-cols-2 md:grid-cols-4 gap-6 max-w-4xl mx-auto">
              <div className="text-center p-6 rounded-xl bg-gradient-to-br from-purple-500/10 to-pink-500/10 border border-purple-500/20">
                <div className="text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">99.2%</div>
                <div className="text-sm text-muted-foreground mt-1">Success Rate</div>
              </div>
              <div className="text-center p-6 rounded-xl bg-gradient-to-br from-pink-500/10 to-orange-500/10 border border-pink-500/20">
                <div className="text-3xl font-bold bg-gradient-to-r from-pink-600 to-orange-600 bg-clip-text text-transparent">2-4h</div>
                <div className="text-sm text-muted-foreground mt-1">Resolution Time</div>
              </div>
              <div className="text-center p-6 rounded-xl bg-gradient-to-br from-orange-500/10 to-yellow-500/10 border border-orange-500/20">
                <div className="text-3xl font-bold bg-gradient-to-r from-orange-600 to-yellow-600 bg-clip-text text-transparent">50K+</div>
                <div className="text-sm text-muted-foreground mt-1">Disputes Resolved</div>
              </div>
              <div className="text-center p-6 rounded-xl bg-gradient-to-br from-purple-500/10 to-blue-500/10 border border-purple-500/20">
                <div className="text-3xl font-bold bg-gradient-to-r from-purple-600 to-blue-600 bg-clip-text text-transparent">7x</div>
                <div className="text-sm text-muted-foreground mt-1">Faster than Banks</div>
              </div>
            </div>
          </section>
        )}
      </main>

      {/* Footer */}
      <footer className="border-t py-12 mt-16 bg-gradient-to-br from-purple-500/5 via-pink-500/5 to-orange-500/5">
        <div className="container mx-auto px-4 md:px-8">
          <div className="text-center mb-6">
            <div className="text-2xl font-bold bg-gradient-to-r from-purple-600 to-pink-500 bg-clip-text text-transparent mb-2">
              UPI Dispute Resolution Agent
            </div>
            <p className="text-muted-foreground text-sm">
              Powered by AI. Trusted by thousands.
            </p>
          </div>
          
          <div className="flex flex-wrap justify-center gap-6 mb-6 text-sm text-muted-foreground">
            <a href="#" className="hover:text-purple-500 transition-colors">About</a>
            <a href="#" className="hover:text-purple-500 transition-colors">Privacy Policy</a>
            <a href="#" className="hover:text-purple-500 transition-colors">Terms of Service</a>
            <a href="#" className="hover:text-purple-500 transition-colors">Contact</a>
          </div>
          
          <div className="text-center">
            <p className="text-sm text-muted-foreground">
              © 2026 UPI Dispute Resolution Agent. 
              <span className="text-purple-500 font-semibold"> Resolve disputes 7x faster.</span>
            </p>
          </div>
        </div>
      </footer>
      </div>
      {/* End of relative z-10 wrapper */}
    </div>
  )
}

export default App
