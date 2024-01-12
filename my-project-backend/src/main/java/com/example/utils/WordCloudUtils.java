package com.example.utils;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.image.AngleGenerator;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

@Component
public class WordCloudUtils {
    public String getWordCloud(List<String> words) {
        // 新建FrequencyAnalyzer 对象
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        // 设置分词返回数量(频率最高的600个词)
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        // 最小分词长度
        frequencyAnalyzer.setMinWordLength(2);
        // 引入中文解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
        // 获取词语频率数据
        final List<WordFrequency> wordFrequencyList = frequencyAnalyzer.load(words);

        // 设置图片分辨率
        Dimension dimension = new Dimension(500, 500);

        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // 不加中文会乱码
        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);
        wordCloud.setKumoFont(new KumoFont(font));

        // 设置边缘留空
        wordCloud.setPadding(2);
        // 设置颜色频率越高用越靠前的颜色
        wordCloud.setColorPalette(new ColorPalette(new Color(0xed1941), new Color(0xf26522), new Color(0x845538)));
        // 设置形状 这里用的圆 参数为半径
        wordCloud.setBackground(new CircleBackground(200));
        // 设置字体大小范围
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
        // 只允许出现水平字体
        wordCloud.setAngleGenerator(new AngleGenerator(0));
        // 设置背景色
        wordCloud.setBackgroundColor(new Color(255, 255, 255));

        // 生成词云
        wordCloud.build(wordFrequencyList);

        OutputStream output = new ByteArrayOutputStream();
        wordCloud.writeToStream("png", output);
        byte[] outputByte = ((ByteArrayOutputStream)output).toByteArray();
        return org.apache.commons.codec.binary.Base64.encodeBase64String(outputByte);

    }
}
