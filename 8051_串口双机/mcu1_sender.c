/*
 * 第一个单片机 — 发送端（Proteus 中 U2）
 *
 * 【必须设置】双击 X2 晶振，频率改为 11.0592MHz
 *
 * 接线：
 *   P1.0 / P1.1 / P1.2  DSW1 前三位拨码（合上为 0，断开为 1）
 *   P3.1 (TXD)  →  U1 的 P3.0 (RXD)
 *
 * 拨码由断开到合上（下降沿）时发送一次指令
 */

#include <reg51.h>

const char code CMD1[] = "15000101";
const char code CMD2[] = "15000102";
const char code CMD3[] = "15000103";

void delay_ms(unsigned int ms)
{
    unsigned int i, j;

    for (i = 0; i < ms; i++) {
        for (j = 0; j < 110; j++) {
            ;
        }
    }
}

void uart_init(void)
{
    TMOD &= 0x0F;
    TMOD |= 0x20;   /* T1 方式2 */
    TH1  = 0xFD;    /* 11.0592MHz → 9600bps */
    TL1  = 0xFD;
    TR1  = 1;
    PCON &= 0x7F;   /* SMOD=0 */
    SCON = 0x40;    /* 方式1，发送 */
    TI   = 1;
}

void uart_send_byte(unsigned char dat)
{
    SBUF = dat;
    while (!TI);
    TI = 0;
}

void uart_send_str(const char code *str)
{
    while (*str) {
        uart_send_byte(*str++);
    }
}

void main(void)
{
    unsigned char cur, last;

    P1   = 0xFF;
    last = 0x07;    /* P1.0~P1.2 初始均为 1（拨码断开） */
    uart_init();

    while (1) {
        cur = P1 & 0x07;

        /* 检测下降沿：1→0 表示拨码合上，发送一次 */
        if ((last & 0x01) && !(cur & 0x01)) {
            uart_send_str(CMD1);
        }
        if ((last & 0x02) && !(cur & 0x02)) {
            uart_send_str(CMD2);
        }
        if ((last & 0x04) && !(cur & 0x04)) {
            uart_send_str(CMD3);
        }

        if (cur != last) {
            delay_ms(50);   /* 消抖 */
            last = P1 & 0x07;
        }
    }
}
