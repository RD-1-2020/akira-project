import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SyslogMessageStats, HourlyStats } from '../models';
import { SyslogMessageService } from '../syslog-message.service';

@Component({
  selector: 'app-syslog-stats-chart',
  templateUrl: './syslog-stats-chart.component.html',
  styleUrls: ['./syslog-stats-chart.component.scss'],
  standalone: true,
  imports: [CommonModule],
})
export class SyslogStatsChartComponent implements OnInit, OnChanges {
  @Input() sourceId?: number;
  
  stats: SyslogMessageStats | null = null;
  loading = false;
  maxCount = 0;

  // Gruvbox colors
  readonly normalColor = '#98971a'; // gruvbox-dark-green
  readonly anomalyColor = '#d65d0e'; // gruvbox-dark-orange

  constructor(private syslogMessageService: SyslogMessageService) {}

  ngOnInit(): void {
    if (this.sourceId) {
      this.loadStats();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['sourceId'] && this.sourceId) {
      this.loadStats();
    }
  }

  loadStats(): void {
    if (!this.sourceId) return;
    
    // Начало сегодня (00:00:00)
    const startOfToday = new Date();
    startOfToday.setHours(0, 0, 0, 0);
    const startDateMillis = startOfToday.getTime();
    
    // Конец сегодня (23:59:59.999)
    const endOfToday = new Date();
    endOfToday.setHours(23, 59, 59, 999);
    const endDateMillis = endOfToday.getTime();
    
    this.loading = true;
    this.syslogMessageService.getStats(this.sourceId, startDateMillis, endDateMillis).subscribe({
      next: (stats) => {
        this.stats = stats;
        this.maxCount = Math.max(...stats.hourlyStats.map(h => h.count), 1);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  getBarHeight(count: number): number {
    return this.maxCount > 0 ? (count / this.maxCount) * 100 : 0;
  }

  getBarColor(hourlyStat: HourlyStats): string {
    const isAnomaly = hourlyStat.isAnomaly ?? hourlyStat.anomaly ?? false;
    return isAnomaly ? this.anomalyColor : this.normalColor;
  }

  formatHour(hour: number): string {
    return `${hour.toString().padStart(2, '0')}:00`;
  }
}

