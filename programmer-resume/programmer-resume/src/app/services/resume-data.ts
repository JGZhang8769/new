import { Injectable } from '@angular/core';
import { of } from 'rxjs';

export interface Resume {
  name: string;
  title: string;
  summary: string;
  email: string;
  phone: string;
  linkedin: string;
  github: string;
  avatar: string;
  experience: Experience[];
  education: Education[];
  skills: string[];
  projects: Project[];
}

export interface Experience {
  company: string;
  title: string;
  period: string;
  description: string;
}

export interface Education {
  school: string;
  degree: string;
  period: string;
}

export interface Project {
  name: string;
  description: string;
  url: string;
  tags: string[];
}

@Injectable({
  providedIn: 'root'
})
export class ResumeData {

  getResumeData() {
    return of({
      name: 'John Doe',
      title: 'Full Stack Developer',
      summary: 'A passionate and creative full-stack developer with 5+ years of experience in building web applications using modern technologies. Proven ability to work independently and as part of a team to deliver high-quality software.',
      email: 'john.doe@email.com',
      phone: '+1 123-456-7890',
      linkedin: 'linkedin.com/in/johndoe',
      github: 'github.com/johndoe',
      avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026704d',
      experience: [
        {
          company: 'Tech Solutions Inc.',
          title: 'Senior Software Engineer',
          period: 'Jan 2020 - Present',
          description: 'Led the development of a new e-commerce platform, resulting in a 30% increase in sales. Mentored junior developers and conducted code reviews.'
        },
        {
          company: 'Web Innovations LLC',
          title: 'Software Engineer',
          period: 'Jun 2017 - Dec 2019',
          description: 'Developed and maintained several client websites using Angular and Node.js. Collaborated with designers to create responsive and user-friendly interfaces.'
        }
      ],
      education: [
        {
          school: 'University of Technology',
          degree: 'Bachelor of Science in Computer Science',
          period: '2013 - 2017'
        }
      ],
      skills: ['Angular', 'TypeScript', 'JavaScript', 'Node.js', 'Express.js', 'MongoDB', 'HTML', 'CSS', 'SCSS', 'Git'],
      projects: [
        {
          name: 'Project Alpha',
          description: 'A personal finance tracker built with Angular and Firebase. Features include budget planning, expense tracking, and financial reports.',
          url: 'github.com/johndoe/project-alpha',
          tags: ['Angular', 'Firebase', 'TypeScript']
        },
        {
          name: 'Project Beta',
          description: 'A real-time chat application using Socket.IO and React. Allows users to create chat rooms and send private messages.',
          url: 'github.com/johndoe/project-beta',
          tags: ['React', 'Socket.IO', 'Node.js']
        }
      ]
    });
  }
}
