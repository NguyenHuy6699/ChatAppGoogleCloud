package com.huy.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huy.baseResponse.BaseResponse;
import com.huy.converter.FriendRequestDTOConverter;
import com.huy.converter.UserDTOConverter;
import com.huy.dto.FriendRequestDTO;
import com.huy.dto.UserDTO;
import com.huy.entity.FriendRequestEntity;
import com.huy.entity.UserEntity;
import com.huy.firebase.message.FcmSender;
import com.huy.repository.FriendRequestRepository;
import com.huy.status.FriendRequestStatus;

@Service
public class FriendRequestServiceImpl {
	@Autowired
	private FriendRequestRepository friendRequestRepo;

	public BaseResponse<Void> sendFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null) {
			System.out.println("friend request. From: " + sender.getUserName() + ". To: " + receiver.getUserName()
					+ " : error(user not exist)");
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		}

		try {
			FcmSender.sendMultiMessage("Lời mời kết bạn mới từ:", sender.getUserName(), sender.getUserName(), receiver);
		} catch (Exception e) {
			System.out.println("Error sending FCM messages");
			e.printStackTrace();
		}

		FriendRequestEntity existFrReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		if (existFrReq != null) {
			System.out.println("friend request. From: " + sender.getUserName() + ". To: " + receiver.getUserName()
					+ " : (request already exist)");
			existFrReq.setStatus(FriendRequestStatus.WAITING);
			friendRequestRepo.save(existFrReq);
		} else {
			FriendRequestEntity frReq = new FriendRequestEntity();
			frReq.setSender(sender);
			frReq.setReceiver(receiver);
			frReq.setStatus(FriendRequestStatus.WAITING);
			friendRequestRepo.save(frReq);
		}

		FriendRequestEntity newFrReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		BaseResponse<Void> res = new BaseResponse<>();
		if (newFrReq != null) {
			System.out.println("Saved friend request. From: " + sender.getUserName() + ". To: " + receiver.getUserName()
					+ " : " + newFrReq);
			res.setOk(true);
			res.setMessage("Gửi kết bạn thành công");
		} else {
			res.setOk(false);
			res.setMessage("Lỗi gửi kết bạn, vui lòng thử lại");
		}

		return res;
	}

	public BaseResponse<UserDTO> getSentFriendRequest(UserEntity user) {
		List<FriendRequestEntity> sentReqList = user.getSentFriendRequest();
		if (sentReqList == null && sentReqList.isEmpty())
			return new BaseResponse<>(false, "Không có lời mời kết bạn gửi đi", null);
		List<UserEntity> sentReqUserEntity = new ArrayList<>();
		List<UserDTO> sentReqUserDto = new ArrayList<>();
		for (FriendRequestEntity req : sentReqList) {
			if (req.getStatus() == FriendRequestStatus.WAITING)
				sentReqUserEntity.add(req.getReceiver());
		}
		if (sentReqUserEntity.size() == 0) {
			return new BaseResponse<>(false, "Không có lời mời kết bạn gửi đi", null);
		}
		sentReqUserDto = UserDTOConverter.getInstance().toListDTO(sentReqUserEntity);

		return new BaseResponse<UserDTO>(true, "Thành công", sentReqUserDto);
	}

	public BaseResponse<UserDTO> getReceivedFriendRequest(UserEntity user) {
		if (user == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		List<FriendRequestEntity> receivedReqList = user.getReceivedFriendRequest();
		if (receivedReqList == null && receivedReqList.isEmpty())
			return new BaseResponse<>(false, "Không có lời mời kết bạn đã nhận", null);
		List<UserEntity> receivedReqUserEntity = new ArrayList<>();
		List<UserDTO> receivedReqUserDto = new ArrayList<>();
		for (FriendRequestEntity req : receivedReqList) {
			if (req.getStatus() == FriendRequestStatus.WAITING)
				receivedReqUserEntity.add(req.getSender());
		}
		if (receivedReqUserEntity.size() == 0) {
			return new BaseResponse<>(false, "Không có lời mời kết bạn đã nhận", null);
		}
		receivedReqUserDto = UserDTOConverter.getInstance().toListDTO(receivedReqUserEntity);
		return new BaseResponse<UserDTO>(true, "Thành công", receivedReqUserDto);
	}

	public BaseResponse<Void> acceptFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		try {
			FriendRequestEntity frReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
			if (frReq != null) {
				frReq.setStatus(FriendRequestStatus.ACCEPTED);
				FriendRequestEntity updatedFrReq = friendRequestRepo.save(frReq);
				FcmSender.sendMultiMessage(receiver.getUserName() + " Đã chấp nhận lời mời kết bạn.", "", "", sender);
				if (updatedFrReq.getStatus() == FriendRequestStatus.ACCEPTED) {
					return new BaseResponse<>(true, "Kết bạn thành công");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BaseResponse<>(false, "Lỗi kết bạn");
	}

	public BaseResponse<Void> cancelFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		FriendRequestEntity frReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		if (frReq != null) {
			frReq.setStatus(FriendRequestStatus.CANCELED);
			FriendRequestEntity updatedFrReq = friendRequestRepo.save(frReq);
			if (updatedFrReq != null && updatedFrReq.getStatus() == FriendRequestStatus.CANCELED) {
				return new BaseResponse<>(true, "Hủy kết bạn thành công");
			}
		}
		return new BaseResponse<>(false, "Lỗi hủy kết bạn");
	}

	public BaseResponse<Void> rejectFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		FriendRequestEntity frReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		if (frReq != null) {
			frReq.setStatus(FriendRequestStatus.REJECTED);
			friendRequestRepo.save(frReq);
			FriendRequestEntity updatedFrReq = friendRequestRepo.save(frReq);
			if (updatedFrReq != null && updatedFrReq.getStatus() == FriendRequestStatus.REJECTED) {
				return new BaseResponse<>(true, "Từ chối kết bạn thành công");
			}
		}
		return new BaseResponse<>(false, "Lỗi từ chối kết bạn");
	}

	public BaseResponse<FriendRequestDTO> getRelationship(UserEntity loggedUser, UserEntity user) {
		FriendRequestEntity frReq1 = friendRequestRepo.findBySenderAndReceiver(loggedUser, user);
		FriendRequestEntity frReq2 = friendRequestRepo.findBySenderAndReceiver(user, loggedUser);

		if (frReq1 != null) {
			if (frReq2 == null 
					|| ((frReq1.getStatus() != FriendRequestStatus.CANCELED && frReq1.getStatus() != FriendRequestStatus.REJECTED)
						&& (frReq2.getStatus() == FriendRequestStatus.CANCELED || frReq2.getStatus() == FriendRequestStatus.REJECTED))
					) {
				return new BaseResponse<FriendRequestDTO>(true, null,
						List.of(FriendRequestDTOConverter.getInstance().toDTO(frReq1)));
			}
		}

		if (frReq2 != null) {
			if (frReq1 == null 
					|| ((frReq2.getStatus() != FriendRequestStatus.CANCELED && frReq2.getStatus() != FriendRequestStatus.REJECTED)
						&& (frReq1.getStatus() == FriendRequestStatus.CANCELED || frReq1.getStatus() == FriendRequestStatus.REJECTED))
					) {
				return new BaseResponse<FriendRequestDTO>(true, null,
						List.of(FriendRequestDTOConverter.getInstance().toDTO(frReq2)));
			}
		}
		
		return new BaseResponse<>(false, "Chưa từng gửi kết bạn", null);
	}

	public boolean removeFriendRequest(UserEntity user1, UserEntity user2) {
		FriendRequestEntity frReq1 = friendRequestRepo.findBySenderAndReceiver(user1, user2);
		if (frReq1 != null) {
			friendRequestRepo.delete(frReq1);
		}

		FriendRequestEntity frReq2 = friendRequestRepo.findBySenderAndReceiver(user2, user1);
		if (frReq2 != null) {
			friendRequestRepo.delete(frReq2);
		}

		if (friendRequestRepo.findBySenderAndReceiver(user2, user1) == null
				&& friendRequestRepo.findBySenderAndReceiver(user1, user2) == null) {
			return true;
		}
		return false;
	}
}
