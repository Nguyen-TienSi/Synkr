using Synkr.Models.Enums;

namespace Synkr.Models.Domain
{
    public class Conversation : BaseEntity
    {
        public string ConversationName { get; set; } = string.Empty;
        public string ConversationDescription { get; set; } = string.Empty;
        public string ConversationPictureUrl { get; set; } = string.Empty;
        public ConversationType ConversationType { get; set; }
        public ICollection<UserDetails> Participants { get; set; } = [];
        public Message? LastMessage { get; set; }
        public DateTime? LastMessageTimesTamp { get; set; } = DateTime.UtcNow;
    }
}
