
import { useCallback, useEffect, useState } from 'react';

import {
  getPatients,
  createPatient,
  updatePatient,
  deletePatient
} from './api/patients';

import './App.css';

const EMPTY_FORM = {
  name: '',
  email: '',
  age: ''
};

function PatientDashboard({ token, role, username, onLogout }) {

  // Information displayed on the screen
  const canEdit = role === 'ADMIN';
  const [patients, setPatients] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [search, setSearch] = useState('');

  // Loading, saving and error messages
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});

  // Get patients from Spring Boot
  const loadPatients = useCallback(async () => {

    setLoading(true);

    try {
      const data = await getPatients(token);
      setPatients(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }

  }, [token]);

  // Run when the page first loads
  useEffect(() => {
    void loadPatients();
  }, [loadPatients]);

  // Update form values when user types
  function handleChange(event) {

    const { name, value } = event.target;

    setForm(previous => ({
      ...previous,
      [name]: value
    }));

    setFieldErrors(previous => ({
      ...previous,
      [name]: undefined
    }));
  }

  // Clear the registration form
  function resetForm() {
    setForm(EMPTY_FORM);
    setEditingId(null);
    setFieldErrors({});
  }

  // Save a new patient or update an existing patient
  async function handleSubmit(event) {

    event.preventDefault();

    setBusy(true);
    setError('');
    setSuccess('');
    setFieldErrors({});

    const patient = {
      name: form.name.trim(),
      email: form.email.trim(),
      age: Number(form.age)
    };

    try {

      if (editingId !== null) {

        await updatePatient(editingId, patient);
        setSuccess('Patient updated successfully!');

      } else {

        await createPatient(patient,token);
        setSuccess('Patient registered successfully!');

      }

      resetForm();
      await loadPatients();

    } catch (err) {

      setError(err.message);
      setFieldErrors(err.fieldErrors || {});

    } finally {
      setBusy(false);
    }
  }

  // Fill the form with an existing patient's information
  function handleEdit(patient) {

    setEditingId(patient.id);

    setForm({
      name: patient.name,
      email: patient.email,
      age: String(patient.age)
    });

    setError('');
    setSuccess('');
    setFieldErrors({});
  }

  // Delete a patient
  async function handleDelete(patient) {

    const confirmed = window.confirm(
      `Are you sure you want to delete ${patient.name}?`
    );

    if (!confirmed) {
      return;
    }

    setBusy(true);
    setError('');
    setSuccess('');

    try {

      await deletePatient(patient.id,token);

      if (editingId === patient.id) {
        resetForm();
      }

      await loadPatients();
    

      setSuccess('Patient deleted successfully!');

    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  // Filter patients based on the search box
  const filteredPatients = patients.filter(patient => {

    const text =
      `${patient.name} ${patient.email} ${patient.id}`;

    return text.toLowerCase().includes(
      search.toLowerCase()
    );
  });

  return (
    <div className="page">

      <header className="header">
        <div>
          <h1>MediCare</h1>
          <p>Patient Management Dashboard</p>
        </div>
        
        <div className="account-actions">
          <span className="environment">
            {username} ({role})
          </span>

          <button
            type="button"
            className="secondary-button"
            onClick={onLogout}
          >
            Logout
          </button>
        </div>

      </header>

      {error && (
        <div className="alert error" role="alert">
          {error}
        </div>
      )}

      {success && (
        <div className="alert success" role="status">
          {success}
        </div>
      )}

<main className={canEdit ? 'dashboard' : 'dashboard read-only'}>
        {canEdit && (
        <section className="panel form-panel">

          <h2>
            {editingId !== null
              ? 'Update Patient'
              : 'Register Patient'}
          </h2>

          <p className="subtitle">
            Enter the patient's information below.
          </p>

          <form onSubmit={handleSubmit}>

            <label htmlFor="name">Full Name</label>
            <input
              id="name"
              name="name"
              type="text"
              placeholder="Enter patient name"
              value={form.name}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={80}
            />
            {fieldErrors.name && (
              <small className="field-error">
                {fieldErrors.name}
              </small>
            )}

            <label htmlFor="email">Email Address</label>
            <input
              id="email"
              name="email"
              type="email"
              placeholder="Enter email"
              value={form.email}
              onChange={handleChange}
              required
            />
            {fieldErrors.email && (
              <small className="field-error">
                {fieldErrors.email}
              </small>
            )}

            <label htmlFor="age">Age</label>
            <input
              id="age"
              name="age"
              type="number"
              placeholder="Enter age"
              value={form.age}
              onChange={handleChange}
              min="0"
              max="120"
              required
            />
            {fieldErrors.age && (
              <small className="field-error">
                {fieldErrors.age}
              </small>
            )}

            <button
              className="primary-button"
              type="submit"
              disabled={busy}
            >
              {busy
                ? 'Saving...'
                : editingId !== null
                  ? 'Update Patient'
                  : 'Register Patient'}
            </button>

            {editingId !== null && (
              <button
                className="secondary-button"
                type="button"
                onClick={resetForm}
                disabled={busy}
              >
                Cancel Editing
              </button>
            )}

          </form>
        </section>
        )}

        <section className="panel list-panel">

          <div className="list-heading">
            <div>
              <h2>Patients</h2>
              <p className="subtitle">
                {patients.length} patient records
              </p>
            </div>

            <button
              className="secondary-button"
              type="button"
              onClick={loadPatients}
              disabled={loading || busy}
            >
              Refresh
            </button>
          </div>

          <input
            className="search-input"
            type="search"
            placeholder="Search patients..."
            aria-label="Search patients"
            value={search}
            onChange={event => setSearch(event.target.value)}
          />

          {loading ? (

            <p className="empty">Loading patients...</p>

          ) : filteredPatients.length === 0 ? (

            <p className="empty">No patients found.</p>

          ) : (

            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Age</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredPatients.map(patient => (
                    <tr key={patient.id}>

                      <td>{patient.id}</td>
                      <td>{patient.name}</td>
                      <td>{patient.email}</td>
                      <td>{patient.age}</td>

                      <td>
                        {canEdit && (
                        <div className="actions">
                          <button
                            className="edit-button"
                            type="button"
                            onClick={() => handleEdit(patient)}
                            disabled={busy}
                          >
                            Edit
                          </button>

                          <button
                            className="delete-button"
                            type="button"
                            onClick={() => handleDelete(patient)}
                            disabled={busy}
                          >
                            Delete
                          </button>
                        </div>
                          )}
                      </td>

                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

          )}

        </section>

      </main>

      <footer>
        MediCare — Java Spring Boot + React Learning Project
      </footer>

    </div>
  );
}

export default PatientDashboard;