import axios from "../axiosConfig";

export const getDonations = async () => {
  const response = await axios.get("/donations");
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
