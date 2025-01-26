export const Roles = {
  ADMIN: "Admin",
  DIETETICIEN: "Diététicien",
  INFIRMIER: "Infirmier",
  PATIENT: "Patient"
};

export function calculateAge(dateOfBirth) {
  const today = new Date();
  const birthDate = new Date(dateOfBirth);

  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDifference = today.getMonth() - birthDate.getMonth();
  const dayDifference = today.getDate() - birthDate.getDate();
  
  if (monthDifference < 0 || (monthDifference === 0 && dayDifference < 0)) {
    age--;
  }

  return age;
}