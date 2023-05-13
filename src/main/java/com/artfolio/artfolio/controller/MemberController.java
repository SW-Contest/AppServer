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

    @GetMapping("/id")
    public ResponseEntity<Object> getMemberById(@PathVariable("id") Long memberId){
        Object memberInfo = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberInfo);
    }

    @DeleteMapping("/id")
    public ResponseEntity<Long> deleteMember(@PathVariable("id") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok(1L);
    }
}
