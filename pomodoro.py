"""
番茄钟桌面应用 - Python + Tkinter
功能：25分钟工作 / 5分钟短休息 / 15分钟长休息 / 番茄计数 / 声音提醒
"""

import tkinter as tk
from tkinter import font as tkfont
import winsound
import threading


class PomodoroTimer:
    # 颜色主题
    COLORS = {
        "bg": "#2C3E50",
        "work_bg": "#E74C3C",
        "short_break_bg": "#27AE60",
        "long_break_bg": "#2980B9",
        "text": "#FFFFFF",
        "timer_text": "#FFFFFF",
        "button_bg": "#34495E",
        "button_fg": "#FFFFFF",
        "button_active": "#4A6A8A",
        "count_text": "#ECF0F1",
    }

    # 时间配置（秒）
    WORK_TIME = 25 * 60
    SHORT_BREAK = 5 * 60
    LONG_BREAK = 15 * 60
    POMODOROS_BEFORE_LONG = 4

    def __init__(self, root):
        self.root = root
        self.root.title("番茄钟")
        self.root.geometry("400x500")
        self.root.resizable(False, False)
        self.root.configure(bg=self.COLORS["bg"])

        self.time_left = self.WORK_TIME
        self.is_running = False
        self.pomodoro_count = 0
        self.current_phase = "work"  # work, short_break, long_break
        self.timer_id = None

        self._setup_ui()
        self._update_display()

    def _setup_ui(self):
        # 状态标签
        self.status_label = tk.Label(
            self.root,
            text="工作中",
            font=tkfont.Font(family="微软雅黑", size=18, weight="bold"),
            bg=self.COLORS["work_bg"],
            fg=self.COLORS["text"],
            width=20,
            height=2,
        )
        self.status_label.pack(pady=(30, 10))

        # 倒计时显示
        self.timer_label = tk.Label(
            self.root,
            text="25:00",
            font=tkfont.Font(family="Consolas", size=72, weight="bold"),
            bg=self.COLORS["bg"],
            fg=self.COLORS["timer_text"],
        )
        self.timer_label.pack(pady=20)

        # 番茄计数
        self.count_label = tk.Label(
            self.root,
            text="🍅 0",
            font=tkfont.Font(family="微软雅黑", size=20),
            bg=self.COLORS["bg"],
            fg=self.COLORS["count_text"],
        )
        self.count_label.pack(pady=10)

        # 按钮框架
        btn_frame = tk.Frame(self.root, bg=self.COLORS["bg"])
        btn_frame.pack(pady=30)

        btn_style = {
            "font": tkfont.Font(family="微软雅黑", size=12),
            "width": 8,
            "height": 2,
            "relief": "flat",
            "cursor": "hand2",
        }

        self.start_btn = tk.Button(
            btn_frame,
            text="开始",
            bg=self.COLORS["button_bg"],
            fg=self.COLORS["button_fg"],
            activebackground=self.COLORS["button_active"],
            command=self.start_timer,
            **btn_style,
        )
        self.start_btn.grid(row=0, column=0, padx=5)

        self.pause_btn = tk.Button(
            btn_frame,
            text="暂停",
            bg=self.COLORS["button_bg"],
            fg=self.COLORS["button_fg"],
            activebackground=self.COLORS["button_active"],
            command=self.pause_timer,
            state="disabled",
            **btn_style,
        )
        self.pause_btn.grid(row=0, column=1, padx=5)

        self.reset_btn = tk.Button(
            btn_frame,
            text="重置",
            bg=self.COLORS["button_bg"],
            fg=self.COLORS["button_fg"],
            activebackground=self.COLORS["button_active"],
            command=self.reset_timer,
            **btn_style,
        )
        self.reset_btn.grid(row=0, column=2, padx=5)

        # 快捷键提示
        tk.Label(
            self.root,
            text="空格: 开始/暂停 | R: 重置",
            font=tkfont.Font(family="微软雅黑", size=9),
            bg=self.COLORS["bg"],
            fg="#7F8C8D",
        ).pack(side="bottom", pady=10)

        # 绑定快捷键
        self.root.bind("<space>", lambda e: self._toggle_start_pause())
        self.root.bind("r", lambda e: self.reset_timer())
        self.root.bind("R", lambda e: self.reset_timer())

    def _toggle_start_pause(self):
        if self.is_running:
            self.pause_timer()
        else:
            self.start_timer()

    def _format_time(self, seconds):
        mins, secs = divmod(seconds, 60)
        return f"{mins:02d}:{secs:02d}"

    def _update_display(self):
        self.timer_label.config(text=self._format_time(self.time_left))
        self.count_label.config(text=f"🍅 {self.pomodoro_count}")

        phase_config = {
            "work": ("工作中", self.COLORS["work_bg"]),
            "short_break": ("短休息", self.COLORS["short_break_bg"]),
            "long_break": ("长休息", self.COLORS["long_break_bg"]),
        }
        status_text, bg_color = phase_config[self.current_phase]
        self.status_label.config(text=status_text, bg=bg_color)

    def start_timer(self):
        if self.is_running:
            return
        self.is_running = True
        self.start_btn.config(state="disabled")
        self.pause_btn.config(state="normal")
        self._tick()

    def pause_timer(self):
        if not self.is_running:
            return
        self.is_running = False
        if self.timer_id:
            self.root.after_cancel(self.timer_id)
            self.timer_id = None
        self.start_btn.config(state="normal")
        self.pause_btn.config(state="disabled")

    def reset_timer(self):
        self.pause_timer()
        self.time_left = self._get_phase_time(self.current_phase)
        self._update_display()

    def _get_phase_time(self, phase):
        return {
            "work": self.WORK_TIME,
            "short_break": self.SHORT_BREAK,
            "long_break": self.LONG_BREAK,
        }[phase]

    def _tick(self):
        if not self.is_running:
            return
        if self.time_left > 0:
            self.time_left -= 1
            self._update_display()
            self.timer_id = self.root.after(1000, self._tick)
        else:
            self._on_phase_complete()

    def _on_phase_complete(self):
        self.is_running = False
        self.start_btn.config(state="normal")
        self.pause_btn.config(state="disabled")

        self._play_notification()

        if self.current_phase == "work":
            self.pomodoro_count += 1
            if self.pomodoro_count % self.POMODOROS_BEFORE_LONG == 0:
                self.current_phase = "long_break"
            else:
                self.current_phase = "short_break"
        else:
            self.current_phase = "work"

        self.time_left = self._get_phase_time(self.current_phase)
        self._update_display()

    def _play_notification(self):
        def beep():
            for _ in range(3):
                winsound.Beep(800, 200)

        threading.Thread(target=beep, daemon=True).start()


def main():
    root = tk.Tk()
    app = PomodoroTimer(root)
    root.mainloop()


if __name__ == "__main__":
    main()
