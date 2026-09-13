package chattychat.sr03_chat_app.dto;

import java.util.List;

public class InviteRequestDTO {

    private List<Long> userIds;

    public InviteRequestDTO() {}

    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
}