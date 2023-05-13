package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.dto.MemberInfo;
import com.artfolio.artfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/save")
    public ResponseEntity<Long> saveMember(@RequestBody MemberInfo memberInfo){
        Long result = memberService.saveMemberInfo(memberInfo);
        return ResponseEntity.ok(result);
    }
}
