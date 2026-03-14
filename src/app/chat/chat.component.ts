import { Component } from '@angular/core';
import { ChatService } from '../services/chat.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent {
  message: string = '';
  chatHistory: { message: string; reply: string; latency: number }[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private chatService: ChatService) {}

  sendMessage() {
    if (!this.message.trim()) return;

    this.loading = true;
    this.error = '';

    this.chatService.sendMessage(this.message).subscribe({
      next: (response: any) => {
        this.chatHistory.push({
          message: this.message,
          reply: response.reply,
          latency: response.latency
        });
        this.message = '';
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to get response. Try again.';
        this.loading = false;
        console.error(err);
      }
    });
  }
}

