namespace Synkr.Models.Domain.Entities
{
    public class Friendship : BaseEntity
    {
        public required UserDetails Requester { get; set; }
        public required UserDetails Addressee { get; set; }
        public bool IsAccepted { get; set; } = false;
    }
}
