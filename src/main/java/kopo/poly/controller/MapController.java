package kopo.poly.controller;


import kopo.poly.dto.MapDTO;
import kopo.poly.dto.RiverCCDTO;
import kopo.poly.service.IMapService;
import kopo.poly.service.IRiverCCService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class MapController {

    private final IMapService mapService;
    private final IRiverCCService riverCCService;

    /*  map 데이터(마커 위경도, 레벨, 지역) 저장  */
    @PostMapping(value = "/map/save")
    public String saveMapData(@RequestBody MapDTO mapDTO, HttpSession session) {
        try {
            log.info("MapController save Start!");

            mapService.saveMapData(mapDTO);

            session.setAttribute("savedMapData", mapDTO); // 세션에 데이터 저장

            log.info("MapController save End!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/map/send";
    }


    @GetMapping(value = "/map/send")
    public String sendMapData(Model model, HttpSession session, ModelMap modelMap) throws Exception {

        /*  로그인 유무 확인  */
        String id = CmmUtil.nvl((String) session.getAttribute("SS_ID"));

        if (id == "") {
            log.info("로그인 정보 없음");

            String msg = "로그인 정보가 없습니다.";
            String url = "/user/login";

            modelMap.addAttribute("msg", msg);
            modelMap.addAttribute("url", url);

            return "/redirect";
        }

        try {
            log.info("MapController send Start!");

            MapDTO savedMapData = (MapDTO) session.getAttribute("savedMapData");

            model.addAttribute("lat", savedMapData.getLat());
            model.addAttribute("lng", savedMapData.getLng());
            model.addAttribute("level", savedMapData.getLevel());
            model.addAttribute("mloc", savedMapData.getMloc());

            log.info("lat : {}", savedMapData.getLat());
            log.info("lng : {}", savedMapData.getLng());
            log.info("level : {}", savedMapData.getLevel());
            log.info("mloc : {}", savedMapData.getMloc());
            log.info("model lat : " + model.getAttribute("lat"));
            log.info("model lng : " + model.getAttribute("lng"));
            log.info("model level : " + model.getAttribute("level"));
            log.info("model mloc : " + model.getAttribute("mloc"));

            log.info("MapController send End!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "/memo/new";
    }

    @GetMapping("/map/riverCCData")
    @ResponseBody
    public ResponseEntity<RiverCCDTO> getCCData(@RequestParam("num") String num) {
        try {
            RiverCCDTO rDTO = riverCCService.getCCData(num);
            return ResponseEntity.ok(rDTO);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();

        }
    }
}

