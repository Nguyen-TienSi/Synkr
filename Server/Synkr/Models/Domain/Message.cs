using Synkr.Models.Enums;

namespace Synkr.Models.Domain
{
    public class Message : BaseEntity
    {
        public string Content { get; set; } = string.Empty;
        public DateTime TimeStamp { get; set; } = DateTime.UtcNow;
        public UserDetails Sender { get; set; } = new UserDetails();
        public Conversation Conversation { get; set; } = new Conversation();
        public MessageType Type { get; set; }
    }
}
