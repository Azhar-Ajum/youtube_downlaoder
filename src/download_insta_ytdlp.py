import sys
import yt_dlp

def download_youtube(url, output_dir):
    ydl_opts = {
        'outtmpl': f'{output_dir}/%(title)s.%(ext)s',
        'format': 'bestvideo+bestaudio/best',
        'merge_output_format': 'mp4',
        'quiet': False,
        'no_warnings': True,
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python download_insta_ytdlp.py <youtube_url> <output_dir>")
        sys.exit(1)

    video_url = sys.argv[1]
    download_dir = sys.argv[2]

    download_youtube(video_url, download_dir)
