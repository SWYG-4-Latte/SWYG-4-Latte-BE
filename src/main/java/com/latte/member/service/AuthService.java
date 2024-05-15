package com.latte.member.service;

import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.member.config.jwt.JwtToken;
import com.latte.member.config.jwt.JwtTokenProvider;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.request.MemberRequest;
import com.latte.member.request.PwChangeRequest;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.response.TempAuthResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class AuthService {


    private final AuthMapper authMapper;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String NUMBER = "0123456789";
    private static SecureRandom random = new SecureRandom();

    public AuthService(AuthMapper authMapper, EmailService emailService, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, ApplicationEventPublisher eventPublisher, JwtTokenProvider jwtTokenProvider) {
        this.authMapper = authMapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.eventPublisher = eventPublisher;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    /**
     * 로그인 요청시 Authentication 객체 생성
     *
     * @param mbrId
     * @param password
     * @return
     */
    @Transactional
    public JwtToken signIn(String mbrId, String password, HttpServletResponse response) throws Exception {
        // 1. mbrId + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mbrId, password);

        try {
            // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
            // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);


            // Refresh Token을 HttpOnly에 설정하고 Cookie에 추가
            Cookie refreshTokenCookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setMaxAge((int) (jwtTokenProvider.getRefreshTokenExpirationMs() / 1000));
            refreshTokenCookie.setSecure(true); // HTTPS 프로토콜에서만 쿠키 전송 가능
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);

            return jwtToken; // 로그인 성공
        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("아이디 또는 비밀번호를 잘못 입력했습니다.");
        }
    }

    /**
     * 회원정보 등록
     * @param request
     */
    @Transactional
    public boolean save(MemberRequest request) {




        // 권한 부여
        request.setRole("USER");
        // 회원탈퇴 여부
        request.setDeleteYn("N");

        request.encodingPassword(passwordEncoder);

        return authMapper.insertMember(request);

    }

/*    *//**
     * 회원아이디 존재유무
     * @param id
     * @return
     *//*
    @Transactional
    public boolean existIdYn(String id) {

        int existId = authMapper.countByLoginId(id);

        if(existId > 0) {
            System.out.println("========아이디가 존재합니다 ==========");
            return false;
        } else {
            return true;
        }

    }


    *//**
     * 회원닉네임 존재유무
     * @param nickname
     * @return
     *//*
    @Transactional
    public boolean existNicknameYn(String nickname) {

        int existNickname = authMapper.countByNickname(nickname);

        if(existNickname > 0) {
            System.out.println("========닉네임이 존재합니다 ==========");
            return false;
        } else {
            return true;
        }

    }*/

    /**
     * 회원정보 수정
     * @param
     */
    //@Transactional
    public boolean update(MemberRequest request) {

            MemberResponse bfResult = getMemberSeq(request.getMbrNo());
            request.updateFrom(bfResult);

            // 권한 부여
            request.setRole("USER");
            // 회원탈퇴 여부
            request.setDeleteYn("N");

            request.encodingPassword(passwordEncoder);


            return authMapper.updateMember(request);


    }


    /**
     * 회원 탈퇴
     *
     * @param id
     */
    @Transactional
    public boolean deleteMember(int id) {

        return authMapper.deleteMember(id);
    }

    /**
     * 회원 수 카운팅 (ID 중복 체크)
     * @param
     * @return 회원 수
     */
    public int countMemberByLoginId(String loginId) {
        return authMapper.countByLoginId(loginId);
    }

    /**
     * 회원 수 카운팅 (Email 중복 체크)
     * @param
     * @return 회원 수
     */
    public int countMemberByEmail(String email) {
        return authMapper.countByEmail(email);
    }


    /**
     * 회원 수 카운팅 (nickname 중복 체크)
     * @param nickname
     * @return
     */
    public int countMemberByNickname(String nickname) {
        return authMapper.countByNickname(nickname);
    }

    /**
     * 회원 수 카운팅(Id 및 Email 중복 체크)
     * @param email
     * @return
     */
    public int countByIdEmail(String id, String email) {
        return authMapper.countByIdEmail(id, email);
    }



    /**
     * 회원 이름과 이메일로 회원 아이디 찾기
     *
     * @param email
     * @return
     */
    public FindIdResponse findIdByNameEmail(String nickname, String email) {

        FindIdResponse member = authMapper.findIdByNameEmail(nickname, email);

        if(member == null) {
            return new FindIdResponse(false, null, null);
        }

        String strUserId = member.getMbrId();


        return new FindIdResponse(true, strUserId, member.getDeleteYn());
    }

    /**
     * 인증번호 발송하기
     * @param mbrId
     * @param email
     * @return
     */
/*    public SendOtpResponse sendOtp(SendOtpRequest request) {

        // 유효회원 여부 검사
        int existsUser = false;
        existsUser = authMapper.countByLoginId(request.getUserId());

        // 인증번호 발송
        if(!existsUser) {
            return new SendOtpResponse(false, "해당 정보로 가입한 아이디가 없습니다.");
        }
        else {
            String resOtp = "false";

            // 발송로직
            resOtp = mailSend.balsongMailSend(request.getUserInfo());


            // 발송된 인증번호 저장
            if(resOtp.equals("false")) {
                return new SendOtpResponse(false, "인증번호 발송에 실패했습니다. 다시 요청해주세요");
            }
            else {
                TbAuthLog ettAuthLog = tbAuthLogRepository.findByUserId(request.getUserId());

                if(ettAuthLog!=null) {
                    ettAuthLog.setAuthNum(resOtp);
                    ettAuthLog.setAuthType(request.getOptType().equals("E") ? "MAIL" : "PN");
                    ettAuthLog.setAuthLoc(request.getUserInfo());
                    ettAuthLog.setModId(request.getUserId());
                    ettAuthLog.setModDate(getToday());
                }
                else {
                    ettAuthLog = new TbAuthLog();
                    ettAuthLog.setUserId(request.getUserId());
                    ettAuthLog.setAuthNum(resOtp);
                    ettAuthLog.setAuthType(request.getOptType().equals("E") ? "MAIL" : "PN");
                    ettAuthLog.setAuthLoc(request.getUserInfo());
                    ettAuthLog.setRegId(request.getUserId());
                    ettAuthLog.setRegDate(getToday());
                    ettAuthLog.setModId(request.getUserId());
                    ettAuthLog.setModDate(getToday());
                }

                tbAuthLogRepository.save(ettAuthLog);
            }

        }

        return new SendOtpResponse(true, "");
    }


    *//**
     * 본인인증번호 비교 확인
     * @param request
     * @return
     *//*

    public CheckOtpResponse checkOtp(CheckOtpRequest request) {
        // 필드 유효성 검사
        if(isNull(request.toString()))			throw new AiconException("[모든] 필드가 비어있습니다.");
        if(isNull(request.getUserId()))			throw new AiconException("[userId] 필드가 비어있습니다.");

        TbAuthLog ettAuthLog = tbAuthLogRepository.findByUserId(request.getUserId());

        if(ettAuthLog == null) {
            return new CheckOtpResponse(false, "해당 정보로 가입한 아이디가 없습니다.");
        }
        if(!ettAuthLog.getAuthNum().equals(request.getAuthNum())) {
            return new CheckOtpResponse(false, "인증번호가 일치하지 않습니다.");
        }
        if(isPassed(ettAuthLog.getModDate(), 5)) {
            return new CheckOtpResponse(false, "유효하지 않은 인증번호입니다.");
        }

        return new CheckOtpResponse(true, "");
    }*/


    /**
     * 회원 리스트
     * @return
     */
/*    public List<MemberResponse> getMemberList() {

        List<MemberResponse> list = authMapper.getMemberList();

        return list;
    }*/

    /**
     * 회원 상세정보 조회
     * @return
     */
    public MemberResponse getMemberInfo(String mbrId) {

        if(authMapper.countByLoginId(mbrId) == 0) {
            throw new BadCredentialsException("아이디가 존재하지 않습니다.");
        }
        return authMapper.findById(mbrId);
    }

    /**
     * 회원 상세정보 조회
     * @return
     */
    public MemberResponse getMemberSeq(int seq) {

        System.out.println("==============service" +  authMapper.findBySeq(seq));
        return authMapper.findBySeq(seq);
    }


    public boolean resetPasswordByEmail(String email) {

        return true;
    }

    @Transactional
    public String saveTempAuthInfo(int seq) throws Exception {
        // 인증번호 생성
        String authNumber = instancePasswordGenerator();

        MemberResponse member = authMapper.findBySeq(seq);

        // 인증번호 정보 넣기
        TempAuthResponse tempAuthInfo = new TempAuthResponse();
        tempAuthInfo.setMbrNo(seq);
        tempAuthInfo.setEmail(member.getEmail());
        tempAuthInfo.setAuthNumber(authNumber);


        String number = emailService.sendEmail(tempAuthInfo);

/*        if(success) {

            // 임시 유저비밀번호로 setting
            PwChangeRequest pwChange = PwChangeRequest.builder().mbrNo(seq).mbrId(member.getMbrId()).password(passwordEncoder.encode(tempPassword)).build();
            //member.setPassword(passwordEncoder.encode(tempPassword));
            authMapper.updatePassword(pwChange);
        }*/

        // 이메일 발송
        return number;
    }

    @Transactional
    public boolean updatePassword(int mbrNo, String password) throws Exception {

        PwChangeRequest pwChange = PwChangeRequest.builder().mbrNo(mbrNo).password(passwordEncoder.encode(password)).build();

        return authMapper.updatePassword(pwChange);
    }

    // 인증번호 생성 메소드
    private static String instancePasswordGenerator() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }


    public MemberResponse getMemberInfoFromToken(String token) {
        // 토큰 유효성 검사
        if (jwtTokenProvider.validateToken(token)) {
            // 토큰에서 회원 식별자(userId) 추출
            Claims claims = jwtTokenProvider.parseClaims(token);
            System.out.println("==============claims");
            System.out.println(claims);
            String userId = claims.get("sub", String.class);

            // 회원 정보 조회
            MemberResponse member = authMapper.findById(userId);
            if (member != null) {
                // 회원 정보를 MemberResponse 객체로 변환하여 반환
                return member;
            }
        }
        return null; // 유효한 토큰이 아니거나 회원 정보가 없는 경우 null 반환
    }

}