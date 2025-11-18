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
