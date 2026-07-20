/*
 * 单片机2（接收端）— 对应 Proteus 中 U1
 *
 * 硬件：
 *   P1.0 — LED1（D1，高电平点亮）
 *   P1.1 — LED2（D2，高电平点亮）
 *   P3.0 (RXD) — 接 U2 的 P3.1 (TXD)
 *   P3.1 (TXD) — 接 U2 的 P3.0 (RXD)
 *   晶振 11.0592MHz
 *
 * 功能：接收 8 字节指令并控制 LED
 *   "15000101" → P1.0 亮，P1.1 灭
 *   "15000102" → P1.0 灭，P1.1 亮
 *   "15000103" → P1.0、P1.1 同时亮
 */

#include <reg51.h>
#include <string.h>

#define CMD_LEN 8

static unsigned char rx_buf[CMD_LEN + 1];
static unsigned char rx_cnt = 0;

void uart_init(void)
{
    TMOD &= 0x0F;
    TMOD |= 0x20;       /* T1 方式2 */
    TH1  = 0xFD;        /* 11.0592MHz → 9600 bps */
    TL1  = 0xFD;
    TR1  = 1;
    PCON &= 0x7F;
    SCON = 0x50;        /* 方式1，允许接收 REN=1 */

    EA  = 1;
    ES  = 1;            /* 允许串口中断 */
}

/* 根据指令控制 P1.0、P1.1，其余 P1 位保持不变 */
void handle_command(void)
{
    rx_buf[CMD_LEN] = '\0';

    /* 先熄灭 P1.0、P1.1 */
    P1 &= 0xFC;

    if (strcmp((char *)rx_buf, "15000101") == 0) {
        P1 |= 0x01;             /* 仅 P1.0 亮 */
    } else if (strcmp((char *)rx_buf, "15000102") == 0) {
        P1 |= 0x02;             /* 仅 P1.1 亮 */
    } else if (strcmp((char *)rx_buf, "15000103") == 0) {
        P1 |= 0x03;             /* P1.0、P1.1 同时亮 */
    }
}

void main(void)
{
    P1 = 0x00;          /* LED 初始全灭 */
    uart_init();

    while (1) {
        ;
    }
}

void uart_isr(void) interrupt 4
{
    if (RI) {
        RI = 0;
        rx_buf[rx_cnt++] = SBUF;

        if (rx_cnt >= CMD_LEN) {
            handle_command();
            rx_cnt = 0;
        }
    }

    if (TI) {
        TI = 0;
    }
}
