namespace Synkr.Models.Domain.Entities
{
    public class BaseEntity
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
        public DateTime DeletedAt { get; set; } = DateTime.UtcNow;
        public bool IsDeleted { get; set; } = false;
        public Guid CreatedBy { get; set; } = Guid.Empty;
        public Guid UpdatedBy { get; set; } = Guid.Empty;
        public Guid DeletedBy { get; set; } = Guid.Empty;
    }
}
