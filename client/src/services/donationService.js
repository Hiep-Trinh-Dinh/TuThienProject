import axios from "../axiosConfig";

export const getDonations = async () => {
  const response = await axios.get("/donations/allnoPaging");
  return response.data;
};

export const updateDonationStatus = async (donationId, status) => {
  const response = await axios.put(`/admin/donations/${donationId}/status`, null, {
    params: { status }
  });
  return response.data;
};

export const getTotalAmountDonationsByDonor = async (donorId) => {
  const response = await axios.get(`/donations/donor/${donorId}/total`);
  return response.data;
};

export const getTotalDonationsByDonor = async (donorId) => {
  const response = await axios.get(`/donations/donor/${donorId}/project-count`);
  return response.data;
};

export const getTotalDonorsByProjectId = async (projectId) => {
  const response = await axios.get(`/donations/project/${projectId}/donor-count`);
  return response.data;
};

export const searchDonations = async (
  paymentMethod,
  paymentStatus,
  from,
  to,
  amountFrom,
  amountTo,
  sortBy,
  page = 0,
  size = 6
) => {
  try {
    const params = new URLSearchParams();

    if (paymentMethod) params.append('paymentMethod', paymentMethod);
    if (paymentStatus) params.append('paymentStatus', paymentStatus);
    if (from) params.append('from', from);
    if (to) params.append('to', to);
    if (amountFrom) params.append('amountFrom', amountFrom);
    if (amountTo) params.append('amountTo', amountTo);
    if (sortBy) params.append('sortBy', sortBy);

    params.append('page', page);
    params.append('size', size);

    const response = await axios.get(`/donations/search?${params}`);

    return response.data;

  } catch (error) {
    console.error("Error searching projects:", error);
    throw error;
  }
};

