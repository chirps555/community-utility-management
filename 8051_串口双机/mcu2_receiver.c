/*
 * 第二个单片机 — 接收端（Proteus 中 U1）
 *
 * 【必须设置】双击 X1 晶振，频率改为 11.0592MHz
 *
 * 接线：
 *   P1.0  D1（高电平亮）
 *   P1.1  D2（高电平亮）
 *   P3.0 (RXD)  ←  U2 的 P3.1 (TXD)
 *
 * 接收 8 字节指令后控制 LED
 */

#include <reg51.h>

sbit LED0 = P1^0;
sbit LED1 = P1^1;

unsigned char rx_buf[8];
unsigned char rx_cnt = 0;

void uart_init(void)
{
    TMOD &= 0x0F;
    TMOD |= 0x20;   /* T1 方式2 */
    TH1  = 0xFD;    /* 11.0592MHz → 9600bps */
    TL1  = 0xFD;
    TR1  = 1;
    PCON &= 0x7F;
    SCON = 0x50;    /* 方式1，允许接收 REN=1 */
    RI   = 0;
    TI   = 0;
}

void process_command(void)
{
    /* 校验指令格式 1500010x */
    if (rx_buf[0] != '1' || rx_buf[1] != '5' || rx_buf[2] != '0' ||
        rx_buf[3] != '0' || rx_buf[4] != '0' || rx_buf[5] != '1' ||
        rx_buf[6] != '0') {
        return;
    }

    LED0 = 0;
    LED1 = 0;

    if (rx_buf[7] == '1') {
        LED0 = 1;
    } else if (rx_buf[7] == '2') {
        LED1 = 1;
    } else if (rx_buf[7] == '3') {
        LED0 = 1;
        LED1 = 1;
    }
}

void uart_recv_byte(unsigned char dat)
{
    /* 帧头同步：第 1 字节必须是 '1' */
    if (rx_cnt == 0) {
        if (dat != '1') {
            return;
        }
    }

    rx_buf[rx_cnt++] = dat;

    if (rx_cnt >= 8) {
        process_command();
        rx_cnt = 0;
    }
}

void main(void)
{
    unsigned char dat;

    P1     = 0x00;  /* P1.0、P1.1 输出低，LED 初始灭 */
    rx_cnt = 0;
    uart_init();

    while (1) {
        if (RI) {
            RI  = 0;
            dat = SBUF;
            uart_recv_byte(dat);
        }
    }
}
