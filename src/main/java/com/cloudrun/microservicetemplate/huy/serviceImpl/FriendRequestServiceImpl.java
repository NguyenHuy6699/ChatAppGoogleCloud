package com.cloudrun.microservicetemplate.huy.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.converter.FriendRequestDTOConverter;
import com.cloudrun.microservicetemplate.huy.converter.UserDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.FriendRequestDTO;
import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.FriendRequestEntity;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.firebase.message.FcmSender;
import com.cloudrun.microservicetemplate.huy.repository.FriendRequestRepository;
import com.cloudrun.microservicetemplate.huy.status.FriendRequestStatus;

@Service
public class FriendRequestServiceImpl {
	@Autowired
	private FriendRequestRepository friendRequestRepo;

	public BaseResponse<FriendRequestDTO> sendFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null) {
			System.out.println("friend request. From: " + sender.getUserName().toString() + ". To: " + receiver.getUserName().toString()
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
		BaseResponse<FriendRequestDTO> res = new BaseResponse<>();
		if (existFrReq != null) {
			System.out.println("friend request. From: " + sender.getUserName() + ". To: " + receiver.getUserName()
					+ " : (request already exist)");
			if (existFrReq.getStatus() == FriendRequestStatus.ACCEPTED) {
				res.setOk(false);
				res.setMessage("Đã là bạn bè");
				return res;
			} else if (existFrReq.getStatus() == FriendRequestStatus.WAITING) {
				res.setOk(true);
				res.setMessage("Kết bạn thành công");
				res.setDataList(List.of(FriendRequestDTOConverter.getInstance().toDTO(existFrReq)));
				return res;
			} else {
				existFrReq.setStatus(FriendRequestStatus.WAITING);
				friendRequestRepo.save(existFrReq);
			}
			
		} else {
			FriendRequestEntity frReq = new FriendRequestEntity();
			frReq.setSender(sender);
			frReq.setReceiver(receiver);
			frReq.setStatus(FriendRequestStatus.WAITING);
			friendRequestRepo.save(frReq);
		}

		FriendRequestEntity newFrReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		if (newFrReq != null) {
			System.out.println("Saved friend request. From: " + sender.getUserName() + ". To: " + receiver.getUserName()
					+ " : " + newFrReq);
			res.setOk(true);
			res.setMessage("Gửi kết bạn thành công");
			res.setDataList(List.of(FriendRequestDTOConverter.getInstance().toDTO(newFrReq)));
		} else {
			res.setOk(false);
			res.setMessage("Lỗi gửi kết bạn, vui lòng thử lại");
		}

		return res;
	}
	
	public Integer getSentFrReqCount(UserEntity sender) {
		List<FriendRequestEntity> sentReqList = sender.getSentFriendRequest();
		int count = 0;
		for (FriendRequestEntity req : sentReqList) {
			if (req.getStatus() == FriendRequestStatus.WAITING) {
				count++;
			}
		}
		return Integer.valueOf(count);
	}
	
	public Integer getReceivedFrReqCount(UserEntity receiver) {
		List<FriendRequestEntity> receivedReqList = receiver.getReceivedFriendRequest();
		int count = 0;
		for (FriendRequestEntity req : receivedReqList) {
			if (req.getStatus() == FriendRequestStatus.WAITING) {
				count++;
			}
		}
		return Integer.valueOf(count);
	}

	public BaseResponse<UserDTO> getSentFriendRequest(UserEntity user) {
		List<FriendRequestEntity> sentReqList = user.getSentFriendRequest();
		if (sentReqList == null || sentReqList.isEmpty())
			return new BaseResponse<>(false, "Không có lời mời kết bạn gửi đi", null);
		List<UserEntity> sentReqUserEntity = new ArrayList<>();
		List<UserDTO> sentReqUserDto = new ArrayList<>();
		for (FriendRequestEntity req : sentReqList) {
			if (req.getStatus() == FriendRequestStatus.WAITING)
				sentReqUserEntity.add(req.getReceiver());
		}
		if (sentReqUserEntity.isEmpty()) {
			return new BaseResponse<>(false, "Không có lời mời kết bạn gửi đi", null);
		}
		sentReqUserDto = UserDTOConverter.getInstance().toListDTO(sentReqUserEntity);

		return new BaseResponse<UserDTO>(true, "Thành công", sentReqUserDto);
	}

	public BaseResponse<UserDTO> getReceivedFriendRequest(UserEntity user) {
		if (user == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		List<FriendRequestEntity> receivedReqList = user.getReceivedFriendRequest();
		if (receivedReqList == null || receivedReqList.isEmpty())
			return new BaseResponse<>(false, "Không có lời mời kết bạn đã nhận", null);
		List<UserEntity> receivedReqUserEntity = new ArrayList<>();
		List<UserDTO> receivedReqUserDto = new ArrayList<>();
		for (FriendRequestEntity req : receivedReqList) {
			if (req.getStatus() == FriendRequestStatus.WAITING)
				receivedReqUserEntity.add(req.getSender());
		}
		if (receivedReqUserEntity.isEmpty()) {
			return new BaseResponse<>(false, "Không có lời mời kết bạn đã nhận", null);
		}
		receivedReqUserDto = UserDTOConverter.getInstance().toListDTO(receivedReqUserEntity);
		return new BaseResponse<UserDTO>(true, "Thành công", receivedReqUserDto);
	}

	public BaseResponse<FriendRequestDTO> acceptFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		try {
			FriendRequestEntity frReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
			BaseResponse<FriendRequestDTO> resp = new BaseResponse<>();
			if (frReq != null) {
				if (frReq.getStatus() == FriendRequestStatus.CANCELED) {
					resp.setOk(false);
					resp.setMessage("Yêu cầu không tồn tại");
					return resp;
				} else {
					frReq.setStatus(FriendRequestStatus.ACCEPTED);
					FriendRequestEntity updatedFrReq = friendRequestRepo.save(frReq);
					FcmSender.sendMultiMessage(receiver.getUserName() + " Đã chấp nhận lời mời kết bạn.", "", "", sender);
					if (updatedFrReq.getStatus() == FriendRequestStatus.ACCEPTED) {
						resp.setOk(true);
						resp.setMessage("Kết bạn thành công");
						resp.setDataList(List.of(FriendRequestDTOConverter.getInstance().toDTO(updatedFrReq)));
						return resp;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BaseResponse<>(false, "Lỗi kết bạn");
	}

	public BaseResponse<FriendRequestDTO> cancelFriendRequest(UserEntity sender, UserEntity receiver) {
		BaseResponse<FriendRequestDTO> resp = new BaseResponse<>();
		if (sender == null || receiver == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		FriendRequestEntity frReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		if (frReq != null) {
			if (frReq.getStatus() == FriendRequestStatus.ACCEPTED) {
				resp.setOk(false);
				resp.setMessage("Đã là bạn bè");
				return resp;
			} else if (frReq.getStatus() == FriendRequestStatus.REJECTED) {
				resp.setOk(true);
				resp.setMessage("Hủy kết bạn thành công");
				resp.setDataList(List.of(FriendRequestDTOConverter.getInstance().toDTO(frReq)));
				return resp;
			} else {
				frReq.setStatus(FriendRequestStatus.CANCELED);
				FriendRequestEntity updatedFrReq = friendRequestRepo.save(frReq);
				if (updatedFrReq != null && updatedFrReq.getStatus() == FriendRequestStatus.CANCELED) {
					resp.setOk(true);
					resp.setMessage("Hủy kết bạn thành công");
					resp.setDataList(List.of(FriendRequestDTOConverter.getInstance().toDTO(updatedFrReq)));
					return resp;
				}
			}
		}
		return new BaseResponse<>(false, "Lỗi hủy kết bạn");
	}

	public BaseResponse<FriendRequestDTO> rejectFriendRequest(UserEntity sender, UserEntity receiver) {
		if (sender == null || receiver == null)
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		FriendRequestEntity frReq = friendRequestRepo.findBySenderAndReceiver(sender, receiver);
		BaseResponse<FriendRequestDTO> resp = new BaseResponse<>();
		if (frReq != null) {
			if (frReq.getStatus() == FriendRequestStatus.CANCELED) {
				resp.setOk(false);
				resp.setMessage("Yêu cầu không tồn tại");
			} else {
				frReq.setStatus(FriendRequestStatus.REJECTED);
				friendRequestRepo.save(frReq);
				FriendRequestEntity updatedFrReq = friendRequestRepo.save(frReq);
				if (updatedFrReq != null && updatedFrReq.getStatus() == FriendRequestStatus.REJECTED) {
					resp.setOk(true);
					resp.setMessage("Từ chối kết bạn thành công");
					resp.setDataList(List.of(FriendRequestDTOConverter.getInstance().toDTO(updatedFrReq)));
					return resp;
				}
			}
		}
		resp.setOk(false);
		resp.setMessage("Lỗi từ chối kết bạn");
		return resp;
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
		
		FriendRequestEntity frEntity = new FriendRequestEntity();
		frEntity.setStatus(FriendRequestStatus.NONE);
		return new BaseResponse<>(false, null, List.of(FriendRequestDTOConverter.getInstance().toDTO(frEntity)));
	}

	public boolean removeFriendRequest(UserEntity user1, UserEntity user2) {
		FriendRequestEntity frReq1 = friendRequestRepo.findBySenderAndReceiver(user1, user2);
		FriendRequestEntity newFrReq1 = null;
		if (frReq1 != null) {
			friendRequestRepo.delete(frReq1);
//			frReq1.setStatus(FriendRequestStatus.REMOVED);
//			newFrReq1 = friendRequestRepo.save(frReq1);
		}

		FriendRequestEntity frReq2 = friendRequestRepo.findBySenderAndReceiver(user2, user1);
		FriendRequestEntity newFrReq2 = null;
		if (frReq2 != null) {
			friendRequestRepo.delete(frReq2);
//			frReq2.setStatus(FriendRequestStatus.REMOVED);
//			newFrReq2 = friendRequestRepo.save(frReq2);
		}

		if (friendRequestRepo.findBySenderAndReceiver(user2, user1) == null
				&& friendRequestRepo.findBySenderAndReceiver(user1, user2) == null) {
			return true;
		}
		return false;
	}
}
