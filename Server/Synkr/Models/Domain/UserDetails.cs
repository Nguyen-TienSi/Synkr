using Synkr.Models.Enums;

namespace Synkr.Models.Domain
{
    public class UserDetails : BaseEntity
    {
        public string FirstName { get; set; } = string.Empty;
        public string LastName { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string PhoneNumber { get; set; } = string.Empty;
        public string Address { get; set; } = string.Empty;
        public string PictureUrl { get; set; } = string.Empty;
        public UserStatus Status { get; set; }
        public string? PasswordHash { get; set; } = string.Empty;
        public DateTime LastSeen { get; set; } = DateTime.UtcNow;
    }
}
