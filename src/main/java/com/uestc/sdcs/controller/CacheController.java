package com.uestc.sdcs.controller;

import com.uestc.sdcs.service.NodeRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CacheController {
    private final NodeRouter nodeRouter;

    @PostMapping("/")
    public ResponseEntity<?> write(@RequestBody Map<String, Object> kv) {
        log.info("write: {}", kv);
        kv.forEach((k, v) -> nodeRouter.write(k, v.toString()));
        return ResponseEntity.ok(kv);
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> read(@PathVariable String key) {
        log.info("read: {}", key);
        String v = nodeRouter.read(key);
        return v == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(Map.of(key, v));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable String key) {
        log.info("delete: {}", key);
        return ResponseEntity.ok(nodeRouter.delete(key));
    }
}
