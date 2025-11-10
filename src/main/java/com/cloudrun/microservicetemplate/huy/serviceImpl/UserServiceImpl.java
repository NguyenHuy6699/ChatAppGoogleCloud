package com.cloudrun.microservicetemplate.huy.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.converter.UserDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.firebase.message.FcmSender;
import com.cloudrun.microservicetemplate.huy.model.Status;
import com.cloudrun.microservicetemplate.huy.repository.UserRepository;
import com.cloudrun.microservicetemplate.huy.service.UserService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Value("${server.port}")
	private String serverPort;
	
	@Value("${google.project.id}")
	private String ggProjectId;
	
	@Value("${google.storage.bucket.name}")
	private String ggBucketName;

	@Override
	public boolean saveUser(UserEntity user) {
		return userRepository.save(user) != null;
	}

	@Override
	public UserEntity findByUserName(String userName) {
		UserEntity userEntity = userRepository.findOneByUserName(userName);
		return userEntity;
	}

	@Override
	public BaseResponse<UserDTO> findAllContacts(String userName) {
		UserEntity userEntity = userRepository.findOneByUserName(userName);
		List<Long> friendIds = userEntity.getFriendIds();
		List<UserDTO> friends = new ArrayList<>();
		for (Long id : friendIds) {
			Optional<UserEntity> friend = userRepository.findById(id);
			if (friend != null) {
				UserDTO contact = UserDTOConverter.getInstance().toDTO(friend.get());
				friends.add(contact);
			}
		}
		BaseResponse<UserDTO> res = new BaseResponse<>(false, "Không thành công", null);
		if (friends.size() > 0) {
			res.setOk(true);
			res.setMessage("Thành công");
			res.setDataList(friends);
		}
		return res;
	}

	@Override
	public Status addFriend(Long userId, Long friendId) {
		Status status = new Status(false, "Thêm bạn thất bại");
		Optional<UserEntity> user = userRepository.findById(userId);
		Optional<UserEntity> friend = userRepository.findById(friendId);
		if (user.get() != null && friend.get() != null) {
			UserEntity userEntity = user.get();
			UserEntity friendEntity = friend.get();
			Status status1 = userEntity.addFriend(friendEntity);
			Status status2 = friendEntity.addFriend(userEntity);
			userRepository.save(userEntity);
			userRepository.save(friendEntity);
			if (status1.isOk() && status2.isOk()) {
				status.setOk(true);
			} else {
				status.setOk(false);
			}
			status.setMessage(status1.getMessage());
		} else {
			status.setOk(false);
			status.setMessage("User không tồn tại");
		}

		return status;
	}

	@Override
	public Status addFriend(UserEntity user1, UserEntity user2) {
		Status status = new Status(false, "Thêm bạn thất bại");
		Status status1 = user1.addFriend(user2);
		Status status2 = user2.addFriend(user1);
		userRepository.save(user1);
		userRepository.save(user2);
		if (status1.isOk() && status2.isOk()) {
			status.setOk(true);
		} else {
			status.setOk(false);
		}
		status.setMessage(status1.getMessage());

		return status;
	}

	@Override
	public Status removeFriend(String userName1, String userName2) {
		UserEntity user1 = userRepository.findOneByUserName(userName1);
		UserEntity user2 = userRepository.findOneByUserName(userName2);
		if (user1 == null || user2 == null) {
			return new Status(false, "Người dùng không tồn tại");
		}
		if (user1.getFriendIds().contains(user2.getId())) {
			user1.getFriendIds().remove(user2.getId());
		}
		if (user2.getFriendIds().contains(user1.getId())) {
			user2.getFriendIds().remove(user1.getId());
		}
		UserEntity updatedUser1 = userRepository.save(user1);
		UserEntity updatedUser2 = userRepository.save(user2);
		if (updatedUser1.getFriendIds().contains(user2.getId())) {
			return new Status(false, "Không thể xóa bạn người dùng: " + userName1);
		}
		if (updatedUser2.getFriendIds().contains(user1.getId())) {
			return new Status(false, "Không thể xóa bạn người dùng: " + userName2);
		}
		return new Status(true, "Xóa bạn thành công");
	}

	@Override
	public BaseResponse<Void> register(String userName, String password, String fullName, String phoneNumber) {
		UserEntity existUser = userRepository.findOneByUserName(userName);
		if (existUser != null) {
			return new BaseResponse<>(false, "Người dùng đã tồn tại");
		}
		UserEntity newUser = new UserEntity();
		newUser.setUserName(userName);
		newUser.setPassword(password);
		newUser.setFullName(fullName);
		newUser.setPhoneNumber(phoneNumber);
		
		UserEntity newUser2 = userRepository.save(newUser);
		if (newUser2 != null) {
			return new BaseResponse<>(true, "Đăng ký thành công");
		}
		return new BaseResponse<>(false, "Đăng ký không thành công");
	}

	@Override
	public BaseResponse<UserDTO> findUserBy(String query) {
		List<UserEntity> userList = userRepository.findByUserNameContainingIgnoreCase(query);
		List<UserDTO> userDtoList = UserDTOConverter.getInstance().toListDTO(userList);
		BaseResponse<UserDTO> res = new BaseResponse<UserDTO>();
		if (userDtoList != null && !userDtoList.isEmpty()) {
			res.setOk(true);
			res.setMessage("Tìm kiếm user: ok");
		} else {
			res.setOk(false);
			res.setMessage("Không tìm thấy kết quả nào");
		}
		res.setDataList(userDtoList);
		return res;
	}

	@Override
	public void sendNotiMessage(String title, String message, String senderUserName, String receiverUserName)
			throws Exception {
		UserEntity receiver = userRepository.findOneByUserName(receiverUserName);
		FcmSender.sendMultiMessage(title, message, senderUserName, receiver);
	}

	@Override
	public String updateAvatar(UserEntity user, MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		Storage storage = StorageOptions.newBuilder().setProjectId(ggProjectId).build().getService();
		BlobId blobId = BlobId.of(ggBucketName, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		byte[] data = file.getBytes();
		
		Storage.BlobTargetOption precondition;
		if (storage.get(ggBucketName, fileName) == null) {
			precondition = Storage.BlobTargetOption.doesNotExist();
		} else {
			precondition = Storage.BlobTargetOption.generationMatch(storage.get(ggBucketName, fileName).getGeneration());
		}
		
		String avatarUrl = null;
		try {
			Blob blob = storage.create(blobInfo, data, precondition);
			if (blob != null) {
				System.out.println(user.getUserName() + "upload avatar success");
				if (blob.getMediaLink() != null) {
					avatarUrl = blob.getMediaLink();
					user.setAvatarUrl(avatarUrl);
					saveUser(user);
				}
			}
		} catch (Exception e) {
			System.out.println(user.getUserName() + "upload avatar failed: " + e.getMessage());
		}
		return avatarUrl;
	}

	@Override
	public BaseResponse<UserDTO> updateProfile(String userName, UserDTO userDto) {
		UserEntity user = userRepository.findOneByUserName(userName);
		if (user == null) {
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		}
		String dtoUserName = userDto.getUserName();
		if (dtoUserName != null && !dtoUserName.equals(userName)) {
			UserEntity duplicateUser = userRepository.findOneByUserName(dtoUserName);
			if (duplicateUser != null) {
				return new BaseResponse<>(false, "Tên đăng nhập đã tồn tại", null);
			}
		}
		
		if (userDto.getUserName() != null && !userDto.getUserName().isBlank())
		user.setUserName(userDto.getUserName());
		
		if (userDto.getAvatarUrl() != null && !userDto.getAvatarUrl().isBlank())
		user.setAvatarUrl(userDto.getAvatarUrl());
		
		if (userDto.getFullName() != null && !userDto.getFullName().isBlank())
		user.setFullName(userDto.getFullName());
		
		if (userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isBlank())
		user.setPhoneNumber(userDto.getPhoneNumber());
		
		try {
			UserEntity updatedUser = userRepository.save(user);
			return new BaseResponse<>(true, "Cập nhật thành công",
					UserDTOConverter.getInstance().toListDTO(List.of(updatedUser)));
		} catch (Exception e) {
			return new BaseResponse<>(false, "Cập nhật không thành công", null);
		}
	}

	@Override
	public BaseResponse<Void> changePassWord(String sessionUserName, String newPassWord) {
		UserEntity user = userRepository.findOneByUserName(sessionUserName);
		if (user == null) {
			return new BaseResponse<>(false, "Người dùng không tồn tại");
		}
		if (user.getPassword().equals(newPassWord)) {
			return new BaseResponse<>(false, "Mật khẩu mới không được trùng mật khẩu cũ");
		}
		user.setPassword(newPassWord);
		try {
			userRepository.save(user);
			return new BaseResponse<>(true, "Cập nhật mật khẩu thành công");
		} catch (Exception e) {
			return new BaseResponse<>(false, "Cập nhật mật khẩu thất bại");
		}
	}
}
