package kopo.poly.controller;


import kopo.poly.dto.MemoDTO;
import kopo.poly.service.IMemoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemoController {

    /**
     * info 상세보기
     * list 목록
     * new  새로쓰기
     * up   수정하기
     */

    // 값 전달은 반드시 DTO 객체를 이용하여 처리


    // 서비스를 안에서 사용할 수 있게 하는 선언문
    private final IMemoService memoService;


    /*  메모 리스트 = "/memo/list"  */
    @GetMapping(value = "/memo/list")
    public String memoList(ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".controller 메모 목록 실행");

        // 메모 리스트 조회
        List<MemoDTO> rList = memoService.getMemoList();

        // 메모 리스트가 없을시 실행
        if (rList == null) {
            rList = new ArrayList<>();
        }

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".controller 메모 목록 종료");

        return "/memo/list";
    }

    /*  메모 검색 = "/memo/search" 추후 수정 필요  */
    @GetMapping(value = "/memo/search")
    public String search(HttpServletRequest request , ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".controller 메모 검색 실행");

        /*  선언 및 입력  */
        String search = CmmUtil.nvl(request.getParameter("search")); // 검색어 html 에서 가져와야함

        /*  데이터 확인  */
        log.info("search : " + search);

        // 메모 리스트  검색 조회
        List<MemoDTO> rList = memoService.searchMemoList();

        // 메모 리스트가 없을시 실행
        if (rList == null) {
            rList = new ArrayList<>();
        }

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".controller 메모 검색 종료");

        return "/memo/list?search"+search;

    }

    /*  메모 작성 페이지 = "/memo/new"  */
    @GetMapping(value = "/memo/new")
    public String MemoInsert() {

        log.info(this.getClass().getName() + ".controller 메모 작성 실행");

        return "/memo/new";
    }

    /*  메모 등록  */
    @GetMapping(value = "/memo/memoInsert")
    public String memoInsert(HttpServletRequest request, ModelMap modelMap, HttpSession session) {

        log.info(this.getClass().getName() + ".controller 메모 등록 실행");

        String msg = "";            // 메시지(알림용)
        String url = "/memo/new";   // 이동할 경로

        try {

            /*  선언 및 입력  */
            String nick = CmmUtil.nvl((String) session.getAttribute("SS_NICK")); // 로그인된 NICK 가져오기
            String title = CmmUtil.nvl(request.getParameter("title"));           // 제목
            String map = CmmUtil.nvl(request.getParameter("map"));               // 이미지 지도
            String contents = CmmUtil.nvl(request.getParameter("contents"));     // 글 내용

            /*  데이터 확인  */
            log.info("nick : " + nick);
            log.info("title : " + title);
            log.info("map : " + map);
            log.info("contents : " + contents);

            /*  데이터 저장  */
            MemoDTO pDTO = new MemoDTO();
            pDTO.setNick(nick);
            pDTO.setTitle(title);
            pDTO.setMap(map);
            pDTO.setContents(contents);

            /*  메모 등록용 비즈니스 로직 호출(쿼리문)  */
            memoService.insertMemoInfo(pDTO);

            // 작성 완료시 유저에게 보여줄 메시지 및 이동할 url
            msg = "등록되었습니다.";
            url = "/memo/list";

        } catch (Exception e) {

            msg = "등록 실패 하였습니다.";

            /*  실패 사유 확인용 로그  */
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        } finally {

            modelMap.addAttribute("msg", msg);
            modelMap.addAttribute("url", url);

            log.info(this.getClass().getName() + ".controller 메모 등록 종료");

        }

        return "/memo/list";  //redirect 가져올시 변경. 현재는 메모 리스트로 이동
    }

    /*  메모 상세보기 = "/memo/info"  */
    @GetMapping(value = "memo/info")
    public String info(HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".controller 메모 상세보기 실행");

        String num = CmmUtil.nvl(request.getParameter("num"));  // 글 번호

        log.info("메모 번호 " + num + "번 입니다.");

        /*  데이터 저장  */
        MemoDTO pDTO = new MemoDTO();
        pDTO.setNum(num);

        /*  상세정보 가져오기  */
        MemoDTO rDTO = Optional.ofNullable(memoService.getMemoInfo(pDTO)).orElseGet(MemoDTO::new);

        /*  조회된 리스트 결과값 넣어주기  */
        modelMap.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".controller 메모 상세보기 종료");

        return "/memo/info";
    }

    /*  메모 수정 페이지 = "memo/up"  */
    @GetMapping(value = "/memo/up")
    public String memoEditInfo(HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".controller 메모 수정페이지 접근 실행");

        String num = CmmUtil.nvl(request.getParameter("num"));

        log.info("메모 번호 " + num + "번 입니다.");

        /*  데이터 저장  */
        MemoDTO pDTO = new MemoDTO();
        pDTO.setNum(num);

        /*  상세정보 가져오기  */
        MemoDTO rDTO = memoService.getMemoInfo(pDTO);
        if (rDTO == null) {
            rDTO = new MemoDTO();
        }

        /*  조회된 리스트 결과값 넣어주기(확인 필요)  */
        modelMap.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".controller 메모 수정페이지 접근 종료");

        return "/memo/up";
    }

    /*  메모 글 수정 실행 로직  */
    @PostMapping(value = "/memo/memoUpdate")
    public String up(HttpSession session, ModelMap modelMap, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".controller 메모 수정 실행");

        String msg = "";
        String url = "";

        try {

            /*  선언 및 입력  */
            String nick = CmmUtil.nvl((String) session.getAttribute("SS_NICK")); // 로그인된 NICK 가져오기
            String title = CmmUtil.nvl(request.getParameter("title"));           // 제목
            String map = CmmUtil.nvl(request.getParameter("map"));               // 이미지 지도
            String contents = CmmUtil.nvl(request.getParameter("contents"));     // 글 내용
            String num = CmmUtil.nvl(request.getParameter("num"));               // 글 번호

            /*  데이터 확인  */
            log.info("nick : " + nick);
            log.info("title : " + title);
            log.info("map : " + map);
            log.info("contents : " + contents);
            log.info("num : " + nick);

            /*  데이터 저장  */
            MemoDTO pDTO = new MemoDTO();
            pDTO.setNick(nick);
            pDTO.setTitle(title);
            pDTO.setMap(map);
            pDTO.setContents(contents);
            pDTO.setNum(num);

            /*  메모 등록용 비즈니스 로직 호출(쿼리문)  */
            memoService.updateMemoInfo(pDTO);

            /* 수정 완료시 사용될 메시지와 이동 경로  */
            msg = "수정 되었습니다.";
            url = "/memo/info?num="+num;

        } catch (Exception e) {

            msg = "수정 실패하였습니다.";


            /*  실패 사유 확인용 로그  */
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        } finally {

            modelMap.addAttribute("msg", msg);
            modelMap.addAttribute("url", url);

            log.info(this.getClass().getName() + ".controller 메모 수정 종료");
        }

        return "/memo/list";
    }

    /*  메모 글 삭제 실행 로직  */
    @GetMapping(value = "/memo/memDelete")
    public String memoDelete(ModelMap modelMap, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".controller 메모 삭제 실행");

        String num = CmmUtil.nvl(request.getParameter("num"));

        log.info("메모 번호 " + num + "번 입니다.");

        String msg = "";
        String url = "/memo/info";

        try {

            /*  데이터 삭제용 글 번호 데이터 저장  */
            MemoDTO pDTO = new MemoDTO();
            pDTO.setNum(num);

            /*  메모 삭제용 비즈니스 로직 호출(쿼리문)  */
            memoService.deleteMemoInfo(pDTO);

            msg = "메모가 삭제되었습니다.";

        } catch (Exception e) {

            msg = "메모 삭제에 실패하였습니다.";

            /*  실패 사유 확인용 로그  */
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        } finally {

            modelMap.addAttribute("msg", msg);
            modelMap.addAttribute("url", url);

            log.info(this.getClass().getName() + ".controller 메모 삭제 종료");

        }

        return "/memo/list";
    }



}