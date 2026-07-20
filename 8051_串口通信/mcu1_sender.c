/*
 * 单片机1（发送端）— 对应 Proteus 中 U2
 *
 * 硬件：
 *   P1.0 / P1.1 / P1.2 — 三个按键（低电平有效，经上拉电阻）
 *   P3.0 (RXD) — 接 U1 的 P3.1 (TXD)
 *   P3.1 (TXD) — 接 U1 的 P3.0 (RXD)
 *   晶振 11.0592MHz（9600 波特率需用此晶振）
 *
 * 功能：按下按键发送对应 8 字节 ASCII 指令
 *   键1 → "15000101"
 *   键2 → "15000102"
 *   键3 → "15000103"
 */

#include <reg51.h>

sbit KEY1 = P1^0;
sbit KEY2 = P1^1;
sbit KEY3 = P1^2;

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
    TMOD |= 0x20;       /* T1 方式2：8位自动重装 */
    TH1  = 0xFD;        /* 11.0592MHz → 9600 bps */
    TL1  = 0xFD;
    TR1  = 1;
    PCON &= 0x7F;       /* SMOD=0，波特率不加倍 */
    SCON = 0x40;        /* 方式1，仅发送 */
}

void uart_send_byte(unsigned char dat)
{
    SBUF = dat;
    while (!TI) {
        ;
    }
    TI = 0;
}

void uart_send_str(const char *s)
{
    while (*s) {
        uart_send_byte(*s++);
    }
}

/* 检测按键，返回 1~3；无键返回 0 */
unsigned char scan_key(void)
{
    if (KEY1 == 0) return 1;
    if (KEY2 == 0) return 2;
    if (KEY3 == 0) return 3;
    return 0;
}

/* 等待按键释放 */
void wait_key_release(void)
{
    while (scan_key() != 0) {
        ;
    }
    delay_ms(20);
}

void main(void)
{
    unsigned char key;

    P1 = 0xFF;          /* P1 口输入，内部上拉 */
    uart_init();

    while (1) {
        key = scan_key();
        if (key == 0) {
            continue;
        }

        delay_ms(20);   /* 消抖 */
        key = scan_key();
        if (key == 0) {
            continue;
        }

        switch (key) {
        case 1:
            uart_send_str("15000101");
            break;
        case 2:
            uart_send_str("15000102");
            break;
        case 3:
            uart_send_str("15000103");
            break;
        default:
            break;
        }

        wait_key_release();
    }
}
