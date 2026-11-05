
import { useState } from 'react';

import { login } from './api/auth';
import PatientDashboard from './PatientDashboard';

import './App.css';

function App() {

  const [session, setSession] = useState(null);

  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('');

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleLogin(event) {

    event.preventDefault();

    setLoading(true);
    setError('');

    try {

      const response = await login(username, password);

      setSession(response);
      setPassword('');

    } catch (err) {

      setError(err.message);

    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    setSession(null);
    setPassword('');
    setError('');
  }

  // Show dashboard after login
  if (session) {
    return (
      <PatientDashboard
        token={session.token}
        role={session.role}
        username={session.username}
        onLogout={handleLogout}
      />
    );
  }

  // Otherwise show the login page
  return (
    <main className="login-page">

      <form className="login-card" onSubmit={handleLogin}>

        <h1>MediCare</h1>

        <p>Sign in to your patient dashboard</p>

        {error && (
          <div className="alert error" role="alert">
            {error}
          </div>
        )}

        <label htmlFor="login-username">Username</label>
        <input
          id="login-username"
          type="text"
          value={username}
          onChange={event => setUsername(event.target.value)}
          autoComplete="username"
          required
        />

        <label htmlFor="login-password">Password</label>
        <input
          id="login-password"
          type="password"
          value={password}
          onChange={event => setPassword(event.target.value)}
          autoComplete="current-password"
          required
        />

        <button
          type="submit"
          className="primary-button"
          disabled={loading}
        >
          {loading ? 'Signing in...' : 'Sign In'}
        </button>

        <small>
          Local development accounts: admin or viewer
        </small>

      </form>

    </main>
  );
}

export default App;
