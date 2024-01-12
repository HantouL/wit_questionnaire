package com.example;

import com.example.utils.WordCloudUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MyProjectBackendApplicationTests {

    @Resource
    WordCloudUtils wordCloudUtils;

    @Test
    void contextLoads() {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));

    }

    @Test
    void testWordCloud(){
        List<String> list = new ArrayList<>();
        list.add("武汉工程大学");
        list.add("武汉大学");
        list.add("武汉理工大学");
        list.add("武汉轻工大学");
        list.add("江汉大学");
        list.add("江汉石油大学");
        list.add("湖北工程学院");
        list.add("理塘丁真");
        list.add("上海理工大学");
        String wordCloud = wordCloudUtils.getWordCloud(list);
        System.out.println(wordCloud);
    }

}
