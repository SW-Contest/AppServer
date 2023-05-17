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
        Long result = memberService.saveMember(memberInfo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMemberById(@PathVariable("id") Long memberId){
        Object memberInfo = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberInfo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMemberByName(@PathVariable("id") String memberName){
        Object memberInfo = memberService.getMemberByName(memberName);
        return ResponseEntity.ok(memberInfo);
    }

    @PutMapping("/{id}/deactive")
    public ResponseEntity<Long> deactivateMember(@PathVariable("id") Long memberId) {
        Long status = memberService.deactivateMember(memberId);
        return ResponseEntity.ok(status);
    }
}