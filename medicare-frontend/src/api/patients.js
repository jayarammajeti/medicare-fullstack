
const BASE_URL = '/api/patients';

async function request(path, token, options = {}) {

  const response = await fetch(BASE_URL + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...options.headers
    }
  });

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const error = new Error(
      data?.message || `Request failed: ${response.status}`
    );

    error.status = response.status;
    error.fieldErrors = data?.fieldErrors || {};

    throw error;
  }

  return data;
}

export function getPatients(token) {
  return request('', token);
}

export function createPatient(patient, token) {
  return request('', token, {
    method: 'POST',
    body: JSON.stringify(patient)
  });
}

export function updatePatient(id, patient, token) {
  return request(`/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(patient)
  });
}

export function deletePatient(id, token) {
  return request(`/${id}`, token, {
    method: 'DELETE'
  });
}
