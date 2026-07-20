/*
 * AT89C51 简易电子琴
 * 硬件连接（Proteus 仿真）：
 *   P1.0~P1.3 - 矩阵键盘列
 *   P1.4~P1.7 - 矩阵键盘行
 *   P0.0~P0.7 - 共阳数码管段选（经排阻 RP1）
 *   P3.0      - 蜂鸣器 LS1
 * 晶振：12MHz
 *
 * 按键音阶映射：
 *   第1行 0,1,2,3   -> do, re, mi, fa
 *   第2行 4,5,6,7   -> sol, la, si, 高音do
 *   第3行 8,9,A,B   -> 高音do, si, la, sol
 *   第4行 C,D,E,F   -> fa, mi, re, do
 */

#include <reg51.h>

sbit BUZZER = P3^0;

/* 共阳数码管段码（低电平点亮），显示 0~F */
const unsigned char code SEG_TAB[] = {
    0xC0, /* 0 */
    0xF9, /* 1 */
    0xA4, /* 2 */
    0xB0, /* 3 */
    0x99, /* 4 */
    0x92, /* 5 */
    0x82, /* 6 */
    0xF8, /* 7 */
    0x80, /* 8 */
    0x90, /* 9 */
    0x88, /* A */
    0x83, /* b */
    0xC6, /* C */
    0xA1, /* d */
    0x86, /* E */
    0x8E  /* F */
};

/*
 * 音阶频率对应的 Timer0 重装值（12MHz，方式1，16位定时）
 * reload = 65536 - 500000 / Hz
 * 索引：0=do 1=re 2=mi 3=fa 4=sol 5=la 6=si 7=高音do
 */
const unsigned int code FREQ_TAB[] = {
    0xF88C, /* do    262Hz */
    0xF98B, /* re    294Hz */
    0xFA15, /* mi    330Hz */
    0xFA67, /* fa    349Hz */
    0xFB04, /* sol   392Hz */
    0xFB90, /* la    440Hz */
    0xFC0C, /* si    494Hz */
    0xFC44  /* 高音do 523Hz */
};

/*
 * 16 键 -> 音阶索引
 * 行优先：0~3 / 4~7 / 8~B / C~F
 */
const unsigned char code NOTE_MAP[] = {
    0, 1, 2, 3,   /* 0,1,2,3   -> do,re,mi,fa */
    4, 5, 6, 7,   /* 4,5,6,7   -> sol,la,si,高音do */
    7, 6, 5, 4,   /* 8,9,A,B   -> 高音do,si,la,sol */
    3, 2, 1, 0    /* C,D,E,F   -> fa,mi,re,do */
};

static unsigned int timer_reload;
static bit sound_on;

void delay_ms(unsigned int ms)
{
    unsigned int i, j;

    for (i = 0; i < ms; i++) {
        for (j = 0; j < 120; j++) {
            /* 空循环 */
        }
    }
}

void timer0_init(void)
{
    TMOD &= 0xF0;
    TMOD |= 0x01;   /* Timer0 方式1 */
    ET0 = 1;
    EA  = 1;
}

void start_tone(unsigned char note_idx)
{
    timer_reload = FREQ_TAB[note_idx];
    TH0 = (unsigned char)(timer_reload >> 8);
    TL0 = (unsigned char)(timer_reload & 0xFF);
    sound_on = 1;
    TR0 = 1;
}

void stop_tone(void)
{
    TR0 = 0;
    sound_on = 0;
    BUZZER = 0;
}

/*
 * 扫描 4x4 矩阵键盘（不等待松手，便于按住持续发声）
 * 返回 0~15 表示按键，0xFF 表示无键
 */
unsigned char scan_keypad(void)
{
    unsigned char row, col, key;

    P1 = 0xF0;
    if ((P1 & 0xF0) == 0xF0) {
        return 0xFF;
    }

    delay_ms(5);

    for (col = 0; col < 4; col++) {
        P1 = (~(0x01 << col) & 0x0F) | 0xF0;

        if ((P1 & 0xF0) != 0xF0) {
            for (row = 0; row < 4; row++) {
                if ((P1 & (0x10 << row)) == 0) {
                    key = row * 4 + col;
                    return key;
                }
            }
        }
    }

    return 0xFF;
}

void main(void)
{
    unsigned char key;
    unsigned char last_key = 0xFF;

    P0 = 0xFF;
    BUZZER = 0;
    timer0_init();

    while (1) {
        key = scan_keypad();
        if (key != 0xFF) {
            P0 = SEG_TAB[key];
            if (key != last_key) {
                start_tone(NOTE_MAP[key]);
                last_key = key;
            }
        } else {
            stop_tone();
            last_key = 0xFF;
        }
    }
}

void timer0_isr(void) interrupt 1
{
    TH0 = (unsigned char)(timer_reload >> 8);
    TL0 = (unsigned char)(timer_reload & 0xFF);
    if (sound_on) {
        BUZZER = !BUZZER;
    }
}
