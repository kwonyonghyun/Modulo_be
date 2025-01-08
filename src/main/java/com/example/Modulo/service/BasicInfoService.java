package com.example.Modulo.service;

import com.example.Modulo.domain.BasicInfo;
import com.example.Modulo.domain.Link;
import com.example.Modulo.domain.Member;
import com.example.Modulo.dto.request.BasicInfoCreateRequest;
import com.example.Modulo.dto.request.BasicInfoUpdateRequest;
import com.example.Modulo.dto.response.BasicInfoResponse;
import com.example.Modulo.exception.BasicInfoNotFoundException;
import com.example.Modulo.exception.BasicInfoUnauthorizedException;
import com.example.Modulo.exception.MemberNotFoundException;
import com.example.Modulo.global.service.S3Service;
import com.example.Modulo.repository.BasicInfoRepository;
import com.example.Modulo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicInfoService {
    private static final String PROFILE_IMAGE_DIRECTORY = "profile-images";

    private final BasicInfoRepository basicInfoRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    private Long getCurrentMemberId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    public Long createBasicInfo(BasicInfoCreateRequest request, MultipartFile profileImage) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, profileImage);
        }

        BasicInfo basicInfo = BasicInfo.builder()
                .member(member)
                .profileImageUrl(profileImageUrl)
                .name(request.getName())
                .email(request.getEmail())
                .careerYear(request.getCareerYear())
                .birthYear(request.getBirthYear())
                .jobPosition(request.getJobPosition())
                .shortBio(request.getShortBio())
                .build();

        List<Link> links = request.getLinks() != null ?
                request.getLinks().stream()
                        .map(linkRequest -> Link.builder()
                                .title(linkRequest.getTitle())
                                .url(linkRequest.getUrl())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        basicInfo.updateLinks(links);
        basicInfo.updateTechStack(request.getTechStack());

        return basicInfoRepository.save(basicInfo).getId();
    }

    private BasicInfo findBasicInfoById(Long id) {
        return basicInfoRepository.findById(id)
                .orElseThrow(BasicInfoNotFoundException::new);
    }

    private void validateMemberAccess(BasicInfo basicInfo) {
        Long currentMemberId = getCurrentMemberId();
        if (!basicInfo.getMember().getId().equals(currentMemberId)) {
            throw new BasicInfoUnauthorizedException();
        }
    }

    @Transactional
    public void updateBasicInfo(Long id, BasicInfoUpdateRequest request, MultipartFile profileImage) {
        BasicInfo basicInfo = findBasicInfoById(id);
        validateMemberAccess(basicInfo);

        String profileImageUrl = basicInfo.getProfileImageUrl();
        if (profileImage != null && !profileImage.isEmpty()) {
            if (profileImageUrl != null) {
                s3Service.deleteFile(profileImageUrl);
            }
            profileImageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, profileImage);
        }

        List<Link> links = request.getLinks() != null ?
                request.getLinks().stream()
                        .map(linkRequest -> Link.builder()
                                .title(linkRequest.getTitle())
                                .url(linkRequest.getUrl())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        basicInfo.update(
                profileImageUrl,
                request.getName(),
                request.getEmail(),
                request.getCareerYear(),
                request.getBirthYear(),
                request.getJobPosition(),
                request.getShortBio()
        );
        basicInfo.updateLinks(links);
        basicInfo.updateTechStack(request.getTechStack());
    }

    @Transactional
    public void deleteBasicInfo(Long id) {
        BasicInfo basicInfo = findBasicInfoById(id);
        validateMemberAccess(basicInfo);

        if (basicInfo.getProfileImageUrl() != null) {
            s3Service.deleteFile(basicInfo.getProfileImageUrl());
        }

        basicInfoRepository.delete(basicInfo);
    }

    public List<BasicInfoResponse> getMyBasicInfos() {
        Long memberId = getCurrentMemberId();
        return basicInfoRepository.findAllByMemberId(memberId).stream()
                .map(BasicInfoResponse::from)
                .collect(Collectors.toList());
    }

    public BasicInfoResponse getBasicInfoById(Long id) {
        BasicInfo basicInfo = findBasicInfoById(id);
        validateMemberAccess(basicInfo);
        return BasicInfoResponse.from(basicInfo);
    }
}